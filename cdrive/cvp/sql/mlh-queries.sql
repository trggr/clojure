inci  select trim(t.incident_id) incident_id, t.open_date_id, t.resolved_date_id
      from smiw.t_incident t,
           smiw.d_company c
      where t.company_id = c.company_id and
            t.incident_id in (':1')
      with ur

incid select trim(t.incident_id) incident_id, t.open_date_id, t.resolved_date_id
      from smiw.t_incident t,
           smiw.d_company c
      where t.company_id = c.company_id and
            t.incident_id in (':1') and
            c.cdir_cd = ':2'
      with ur

incil select trim(t.incident_id) incident_id, t.open_date_id, t.resolved_date_id
      from smiw.t_incident t,
           smiw.d_company c
      where t.company_id = c.company_id and
            t.incident_id like ':1' and
            c.cdir_cd = ':2'
      fetch first 30 rows only
      with ur

chg   select trim(t.change_id) change_id, c.cdir_cd, t.closed_date_id, t.cancelled_state
      from smiw.t_change t,
           smiw.d_company c
      where t.company_id = c.company_id and
            t.change_id in (':1')
            with ur

chgd  select trim(t.change_id) change_id, c.cdir_cd, t.closed_date_id, t.cancelled_state
      from smiw.t_change t,
           smiw.d_company c
      where t.company_id = c.company_id and
            t.change_id in (':1') and
            c.cdir_cd = ':2'
            with ur

dcomp  select company_id, company_name, cdir_cd, boarded
       from busintel.d_company
       where lower(company_name) like '%:1%'
       order by upper(company_name)
       with ur

dmetr  select metric_id, metric_name, metric_code, data_source,
              numerator_metric_code, denominator_metric_code
       from busintel.d_metric
       where lower(data_source) like '%:1%'
       order by upper(metric_name)
       with ur

compare select dim_name, metric_name, metric_id, value_avg
        from busintel.compare
        where dim_type_id = :1 and
              report_month = ':2'
        order by dim_name, metric_name
        with ur

testq  select current timestamp from sysibm.sysdummy1 with ur

inc-t-c select c.company_name, c.cdir_cd,
        trim(t.incident_id) incident_id, t.open_date_id, t.resolved_date_id, t.priority_id,
             e.open_dttm, e.occurred_dttm, e.resolved_dttm,
             e.priority_cd, e.resolver_group_cd, e.contact_name,
             e.incid_desc
        from smiw.t_incident t, smiw.d_company c, smiw.t_incident_detail e
        where t.company_id = c.company_id and
              t.incident_id like '%:1%' and
              c.cdir_cd = ':2' and
              t.id = e.transaction_ref_id
        with ur

