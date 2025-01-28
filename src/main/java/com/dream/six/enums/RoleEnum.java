package com.dream.six.enums;


public enum RoleEnum {

    ROLE_SUPER_ADMIN("Super Admin"),
    ROLE_CC("Campaign Coordinator"),
    ROLE_RL("Research Leader"),
    ROLE_RA("Research Analyst"),
    ROLE_QL("Quality Leader"),
    ROLE_QA1("Quality Analyst First"),
    ROLE_QA2("Quality Analyst Second");


    //'1 =>''Super Admin'',2 =>''Campaign Coordinator'',3 =>''Research Manager'',4 =>''Quality Manager'',5 =>''Research Agent'',6 =>''Quality Agent'',7 =>''Quality Leader'',8 =>''Research Leader'',9 =>''MIS'',10 =>''Campaign Coordinator Delivery''',

    private final String statusValue;

     RoleEnum(String statusValue) {
        this.statusValue = statusValue;
    }
}
