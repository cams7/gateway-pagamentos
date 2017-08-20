select * from QRTZ_JOB_DETAILS;
select * from QRTZ_TRIGGERS;
select * from QRTZ_SIMPLE_TRIGGERS;
select * from QRTZ_CRON_TRIGGERS;
select * from QRTZ_SIMPROP_TRIGGERS;
select * from QRTZ_BLOB_TRIGGERS;

select jd.sched_name, jd.job_name, jd.job_group from QRTZ_JOB_DETAILS jd;

select jd.job_name, t.trigger_name from QRTZ_JOB_DETAILS jd inner join QRTZ_TRIGGERS t on (jd.sched_name=t.sched_name and jd.job_name=t.job_name and jd.job_group=t.job_group);

select jd.job_name, t.trigger_name, st.repeat_interval from QRTZ_JOB_DETAILS jd 
inner join QRTZ_TRIGGERS t on (jd.sched_name=t.sched_name and jd.job_name=t.job_name and jd.job_group=t.job_group)
inner join QRTZ_SIMPLE_TRIGGERS st on (t.sched_name=st.sched_name and t.trigger_name=st.trigger_name and t.trigger_group=st.trigger_group);

select jd.job_name, t.trigger_name, ct.cron_expression from QRTZ_JOB_DETAILS jd 
inner join QRTZ_TRIGGERS t on (jd.sched_name=t.sched_name and jd.job_name=t.job_name and jd.job_group=t.job_group)
inner join QRTZ_CRON_TRIGGERS ct on (t.sched_name=ct.sched_name and t.trigger_name=ct.trigger_name and t.trigger_group=ct.trigger_group);

select jd.job_name, t.trigger_name, st.str_prop_1 from QRTZ_JOB_DETAILS jd 
inner join QRTZ_TRIGGERS t on (jd.sched_name=t.sched_name and jd.job_name=t.job_name and jd.job_group=t.job_group)
inner join QRTZ_SIMPROP_TRIGGERS st on (t.sched_name=st.sched_name and t.trigger_name=st.trigger_name and t.trigger_group=st.trigger_group);

select jd.job_name, t.trigger_name, bt.blob_data from QRTZ_JOB_DETAILS jd 
inner join QRTZ_TRIGGERS t on (jd.sched_name=t.sched_name and jd.job_name=t.job_name and jd.job_group=t.job_group)
inner join QRTZ_BLOB_TRIGGERS bt on (t.sched_name=bt.sched_name and t.trigger_name=bt.trigger_name and t.trigger_group=bt.trigger_group);










