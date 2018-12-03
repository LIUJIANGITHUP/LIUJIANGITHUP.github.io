package cn.pojo;

public class Information {
   private String BUG_ID;  //缺陷id
   private String HEAD_PERSON;//负责人
   private Integer REOPEN_NUMBER;//reopen次数
   private String VERSION_NAME;//发布版本号/项目名称
   
   public Information() {
	   
   }

public String getBUG_ID() {
	return BUG_ID;
}

public void setBUG_ID(String bUG_ID) {
	BUG_ID = bUG_ID;
}

public String getHEAD_PERSON() {
	return HEAD_PERSON;
}

public void setHEAD_PERSON(String hEAD_PERSON) {
	HEAD_PERSON = hEAD_PERSON;
}

public Integer getREOPEN_NUMBER() {
	return REOPEN_NUMBER;
}

public void setREOPEN_NUMBER(Integer rEOPEN_NUMBER) {
	REOPEN_NUMBER = rEOPEN_NUMBER;
}

public String getVERSION_NAME() {
	return VERSION_NAME;
}

public void setVERSION_NAME(String vERSION_NAME) {
	VERSION_NAME = vERSION_NAME;
}

@Override
public String toString() {
	return "Information [BUG_ID=" + BUG_ID + ", HEAD_PERSON=" + HEAD_PERSON + ", REOPEN_NUMBER=" + REOPEN_NUMBER
			+ ", VERSION_NAME=" + VERSION_NAME + "]";
}
   
   
}
