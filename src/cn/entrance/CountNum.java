package cn.entrance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.pojo.Information;
import cn.utils.CreatExcel;
import cn.utils.JDBCUtils;
import cn.utils.JDBCUtils2;
import cn.utils.JDBCUtils3;

/*
 * poi读取excel文件并执行sql
 */
public class CountNum {
		/**
		 *
		 * @param cell
		 *            一个单元格的对象
		 * @return 返回该单元格相应的类型的值
		 */
		public static Object getRightTypeCell(Cell cell) {
			Object object = null;
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING: {
				object = cell.getStringCellValue();
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: {
				if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
					SimpleDateFormat sdf = null;
					if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
							.getBuiltinFormat("h:mm")) {
						sdf = new SimpleDateFormat("HH:mm");
					} else {// 日期
						sdf = new SimpleDateFormat("yyyy-MM-dd");
					}
					Date date = cell.getDateCellValue();
					object = sdf.format(date);
				} else {
					object = cell.getNumericCellValue();
				}
				break;
			}
			case Cell.CELL_TYPE_FORMULA: {
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				object = cell.getNumericCellValue();
				break;
			}
			case Cell.CELL_TYPE_BLANK: {
				object = cell.getStringCellValue();
				break;
			}
			}
			return object;
		}

		/**
		 * 读取出filePath中的所有数据信息
		 * 
		 * @param filePath
		 *            excel文件的绝对路径
		 * @throws SQLException 
		 * 
		 */
		@SuppressWarnings("deprecation")
		public static Map<Integer, List<Information>> getDataFromExcel(String filePath) throws SQLException {
			Map<Integer,List<Information>> ll=new HashMap<Integer, List<Information>>();
			List<Information> l1=new ArrayList<Information>();
			List<Information> l2=new ArrayList<Information>();
			List<Information> l3=new ArrayList<Information>();
			// 判断是否为excel类型文件
			if (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx")) {
				System.out.println("文件不是excel类型");
			}
			FileInputStream fis = null;
			Workbook wookbook = null;
			int flag = 0;
			try {
				// 获取一个绝对地址的流
				fis = new FileInputStream(filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				// 2003版本的excel，用.xls结尾
				wookbook = new HSSFWorkbook(fis);// 得到工作簿
			} catch (Exception ex) {
				try {
					// 2007版本的excel，用.xlsx结尾
					wookbook = new XSSFWorkbook(filePath);// 得到工作簿
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 得到一个工作表
			Sheet sheet = wookbook.getSheetAt(0);
			// 获得表头
			Row rowHead = sheet.getRow(0);
			// 根据不同的data放置不同的表头
			Map<Object, Integer> headMap = new HashMap<Object, Integer>();
			try {
				// ----------------这里根据你的表格有多少列
				while (flag < rowHead.getPhysicalNumberOfCells()) {
					Cell cell = rowHead.getCell(flag);
					if (getRightTypeCell(cell).toString().equals("统计项目的来源")) {
						headMap.put("PROJECT_SORCE", flag);
					}if (getRightTypeCell(cell).toString().equals("项目名称")) {
						headMap.put("PROJECT_NAME", flag);
					}if (getRightTypeCell(cell).toString().equals("Reopen次数")) {
						headMap.put("REOPEN_NUMBER", flag);
					}
					flag++;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("表头不合规范，请修改后重新导入");
			}
			// 获得数据的总行数
			int totalRowNum = sheet.getLastRowNum();
			if (0 == totalRowNum) {
				System.out.println("Excel内没有数据！");
			}
			Cell cell_1 = null,cell_2 = null, cell_3 = null;
			// 获得所有数据
			for (int i = 1; i <= totalRowNum; i++) {
				// 获得第i行对象
				Row row = sheet.getRow(i);
				try {
					cell_1 = row.getCell(headMap.get("PROJECT_SORCE"));
					cell_2 = row.getCell(headMap.get("PROJECT_NAME"));
					cell_3 = row.getCell(headMap.get("REOPEN_NUMBER"));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("获取单元格错误");
				}
				try {
					String PROJECT_SORCE  = (String) getRightTypeCell(cell_1);        
					String PROJECT_NAME = (String) getRightTypeCell(cell_2);
					String REOPEN_NUMBER = (String) getRightTypeCell(cell_3);
					Connection connection=null;
					//如果选择公司qc
					if(PROJECT_SORCE.equals("公司QC")) {
						//创建数据库连接
						connection=JDBCUtils.getConnection();
						PreparedStatement ppstat=null;
						try {
							String sql="select t0.BG_BUG_ID QCID,t0.BG_RESPONSIBLE developer, count(t0.BG_BUG_ID)  countreopen, t0.bg_planned_closing_ver version  from (select ap.ap_property_id,b.BG_BUG_ID, b.BG_RESPONSIBLE,b.BG_DETECTION_DATE,b.bg_planned_closing_ver from "+PROJECT_NAME+".audit_properties ap left join "+PROJECT_NAME+".audit_log al on al.AU_ACTION_ID = ap.ap_action_id  left join "+PROJECT_NAME+".bug b on b.BG_BUG_ID = al.au_entity_id where ap.ap_new_value = 'Reopen') t0 group by t0.BG_BUG_ID, t0.BG_RESPONSIBLE,t0.bg_planned_closing_ver having count(t0.BG_BUG_ID) > ? order by t0.BG_BUG_ID";
							ppstat=connection.prepareStatement(sql);
							ppstat.setInt(1,Integer.parseInt(REOPEN_NUMBER));
							ResultSet rs1 = ppstat.executeQuery();
							while(rs1.next()) {
								Information information=new Information();
								information.setBUG_ID(rs1.getString("QCID"));
								information.setHEAD_PERSON(rs1.getString("developer"));
								information.setREOPEN_NUMBER(rs1.getInt("countreopen"));
								information.setVERSION_NAME(rs1.getString("version"));
								l1.add(information);
							}
							JDBCUtils.closeAll(ppstat, connection);
						} catch (Exception e) {
							e.printStackTrace();
						}		
					}else if(PROJECT_SORCE.equals("项目管理平台")) {
						connection=JDBCUtils3.getConnection();
						PreparedStatement ppstat=null;
						try {
							String sql="SELECT  bugpro.bugid QCID,bugpro.NAME developer,count(bugpro.bugid) countreopen,bugpro.custom_301 version FROM (SELECT b.BugId,(SELECT n.title FROM SubProject n WHERE n.ProjectID = s.ProjectID AND n.SubProjectID = dbo.Fun_SubProjectGetParentSubId (s.SubProjectID, 98) AND n.ProjectID = 202) title,(SELECT dbo.Fun_GetStrArrayStrOfIndex (replace(CAST ((c.Custom_309) AS nvarchar (1000)),' ','-'),'-',(SELECT COUNT (result) FROM dbo.Fun_SplitStr (replace(CAST ((c.Custom_309) AS nvarchar (1000)),' ','-'),'-')) - 1)) NAME,c.Custom_301 FROM CustomerFieldTrackExt c,Bug b,SubProject s WHERE b.SubProjectID = s.SubProjectID AND b.ProjectID = s.ProjectID AND b.BugID = c.BugID AND c.ProjectID = s.ProjectID AND s.ProjectID = 202) bugpro,changelog cl WHERE bugpro.BugId = cl.bugid AND cl.projectid = 202 AND changelog LIKE N'%为''Reopen''%'   group by bugpro.BUGID,bugpro.title,bugpro.NAME,bugpro.custom_301 having count(bugpro.BUGID) > ? and bugpro.title=? order by bugpro.BUGID";
							ppstat = connection.prepareStatement(sql);
							ppstat.setInt(1,Integer.parseInt(REOPEN_NUMBER));
							ppstat.setString(2,PROJECT_NAME);
							ResultSet r2 = ppstat.executeQuery();
							while(r2.next()) {
								Information information =new Information();
								information.setBUG_ID(r2.getString("QCID"));//缺陷id
								information.setHEAD_PERSON(r2.getString("developer"));//负责人
								information.setREOPEN_NUMBER(r2.getInt("countreopen"));//reopen次数
								information.setVERSION_NAME(r2.getString("version"));// 发布版本号或项目名称
								l2.add(information);
							}
							JDBCUtils.closeAll(ppstat, connection);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if(PROJECT_SORCE.equals("计量中心")) {
						connection=JDBCUtils2.getConnection();
						PreparedStatement ppstat=null;
						try {
							String sql="select t0.BG_BUG_ID QCID,t0.BG_RESPONSIBLE developer, count(t0.BG_BUG_ID)  countreopen,t0.bg_user_13 version from (select ap.ap_property_id,b.BG_BUG_ID,b.BG_RESPONSIBLE,b.BG_DETECTION_DATE,b.bg_user_13 from "+PROJECT_NAME+".audit_properties ap left join "+PROJECT_NAME+".audit_log al on al.AU_ACTION_ID = ap.ap_action_id left join "+PROJECT_NAME+".bug b on b.BG_BUG_ID = al.au_entity_id where ap.ap_new_value = 'Reopen') t0 group by t0.BG_BUG_ID, t0.BG_RESPONSIBLE,t0.bg_user_13 having count(t0.BG_BUG_ID) > ? order by t0.BG_BUG_ID";
							ppstat = connection.prepareStatement(sql);
						    ppstat.setInt(1,Integer.parseInt(REOPEN_NUMBER));
						    ResultSet r3 = ppstat.executeQuery();
						    while(r3.next()) {
						    	Information information =new Information();
						    	information.setBUG_ID(r3.getString("QCID"));
						    	information.setHEAD_PERSON(r3.getString("developer"));
						    	information.setREOPEN_NUMBER(r3.getInt("countreopen"));
						    	information.setVERSION_NAME(r3.getString("version"));
						    	l3.add(information);
						    }
						    JDBCUtils.closeAll(ppstat, connection);
						} catch (Exception e) {
							e.printStackTrace();
						}
					    
					}
				}catch (ClassCastException e) {
					JOptionPane.showMessageDialog(null, e);
					System.out.println("数据不全是数字或全部是文字!");
				}
			}
			ll.put(0,l1);ll.put(1,l2);ll.put(2,l3);
			return ll;
		}

		public static void main(String[] args) throws Exception {
			//System.getProperty("user.dir")为程序所在目录 
	       int isPerform = JOptionPane.showConfirmDialog(null, "是否执行", "提示", JOptionPane.YES_NO_OPTION);
	       if(isPerform==JOptionPane.YES_OPTION) {
				try {
					//Map<Integer, List<Information>> dataFromExcel = getDataFromExcel("D:" + File.separator+"data.xlsx");
					Map<Integer, List<Information>> dataFromExcel = getDataFromExcel(System.getProperty("user.dir")+ File.separator+"data.xlsx");
					System.out.println(dataFromExcel);
					//将派工数据写入到执行excel中 
					CreatExcel.creat2(dataFromExcel);
					JOptionPane.showMessageDialog(null, "执行成功");
				} catch (Exception e) {
					logger.info(e.getMessage());
				    e.printStackTrace();
					JOptionPane.showMessageDialog(null, e);
				}
	        }
		}
		//运行时日志写入jar包所在文件夹下
		static Logger logger = Logger.getLogger(CountNum.class.getName());	
		 static {
		        String path = new File("").getAbsolutePath();
		        FileAppender appender = (FileAppender) org.apache.log4j.Logger.getRootLogger().getAppender("R");
		        appender.setFile(path + File.separator + "test.log");
		    }

	}
