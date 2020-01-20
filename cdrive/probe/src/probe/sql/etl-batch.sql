with x as (
  select d.yyyymm, e.process_name, to_char(max(e.version), 'mm/dd/yyyy') ver
  from busintel.etl_batch e, busintel.d_date d
  where e.batch_upto > timestamp_iso(d.month_first_day) and
        e.batch_from < (timestamp_iso(d.month_first_day) + 1 month) and
        e.status = 'OK' and d.year >= 2009 and d.day_of_month = 1
  group by d.yyyymm, e.process_name
)
  select x.yyyymm mon, x.process_name process, x.ver
  from x, busintel.data_source d
  where x.process_name = d.data_source_cd
  order by x.yyyymm, d.report_order

