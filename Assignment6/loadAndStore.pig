geeks = load 'hdfs://localhost:9000/inputs/mr_inputs/info.txt' using  PigStorage(',') as (name,age,degree,city,company);

trimgeeks = limit geeks 6;
dump trimgeeks;

store geeks into 'hdfs://localhost:9000/inputs/pig_inputs/geeks_info';

name_city = foreach geeks generate $0 as name,  $3 as city;

register 'hdfs://localhost:9000/inputs/pig_inputs/PIG_MyUdf.jar'
define concat_fun org.chandan.hadoop.pig.sample.SampleUdf(); 
concat_result = foreach geeks generate concat_fun($0,'from',$3);
concat_resulttrim = limit concat_result 6;
dump concat_resulttrim;



geeks_group = group trimgeeks all;

count_no = foreach geeks_group generate count_star(trimgeeks);
dump count_no;
