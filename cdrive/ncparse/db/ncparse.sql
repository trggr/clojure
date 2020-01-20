drop table business_address@
drop table FILER@
drop table FILING_VALUES@
drop table BUSINESS_ADDRESS@
drop table SUBMISSION@
drop table COMPANY_DATA@
drop table MAIL_ADDRESS@
drop table FORMER_COMPANY@
drop table SERIES@
drop table CLASS_CONTRACT@
drop table DOCUMENT@
drop table FILED_FOR@
create table former_company (
  filed_for_id varchar(50),
  former_company_id varchar(50),
  date_changed varchar(50),
  former_conformed_name varchar(50)
)@
alter table submission add effectiveness_date varchar(40)
@
