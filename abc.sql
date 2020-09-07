SELECT o.* FROM sc o HAVING 
(SELECT COUNT(*) FROM sc WHERE o.CId = CId AND o.score < score) + 1 <= 3 
ORDER BY CId, score DESC, SId;


查看，发现结果是47行的一个表，列出了类似 01号课里“30分小于50，也小于70，也小于80，也小于90”“50分小于70，小于80，小于90”.....
所以理论上，对任何一门课来说，分数最高的那三个记录，在这张大表里，通过a.sid和a.cid可以联合确定这个同学的这门课的这个分数究竟比多少个其他记录高/低，
如果这个特定的a.sid和a.cid组合出现在这张表里的次数少于3个，那就意味着这个组合（学号+课号+分数）是这门课里排名前三的。
所以下面这个计算中having count 部分其实count()或者任意其他列都可以，这里制定了一个列只是因为比count()运行速度上更快。
















-- 1.查询" 01 "课程比" 02 "课程成绩高的学生的信息及课程分数
解法1，创建多个子查询组合生成新表，再从新表中查询所需要的结果。 听说子查询的效率较低；
SELECT st.*, class1, class2 FROM student st JOIN 
(SELECT * FROM 
(SELECT SId s1, score class1 FROM sc WHERE CId = '01') c1, 
(SELECT SId s2, score class2 FROM sc WHERE CId = '02') c2
WHERE s1 = s2 AND class1 > class2) c 
ON st.SId = c.s1;

解法2，自己写的
SELECT *
FROM score AS sc1
INNER JOIN score AS sc2 ON sc1.Sid = sc2.Sid
WHERE sc1.Cid = 01 AND sc2.Cid = 02 AND sc1.score > sc2.score 
GROUP BY sc1.Sid



-- 1.1 查询同时存在" 01 "课程和" 02 "课程的情况
别人的答案
SELECT * FROM
((SELECT * FROM sc WHERE CId = '01') t1
JOIN
(SELECT * FROM sc WHERE CId = '02') t2
ON
t1.Sid = t2.Sid);

我自己的答案
select sc1.*,sc2.*,st.*
from score  as sc1
inner join score as sc2 on sc1.Sid = sc2.Sid
INNER JOIN student AS st ON sc1.Sid = st.Sid
where sc1.Cid = 01 and sc2.Cid = 02
group by sc1.Sid;



-- 1.2 查询存在" 01 "课程但可能不存在" 02 "课程的情况(不存在时显示为 null )
SELECT * FROM
((SELECT * FROM sc WHERE CId = '01') t1
left JOIN
(SELECT * FROM sc WHERE CId = '02') t2
ON
t1.Sid = t2.Sid);

-- 1.3 查询不存在" 01 "课程但存在" 02 "课程的情况
select * from sc 
where sc.CId = '02' AND 
sc.SId NOT IN
(SELECT SId FROM sc WHERE sc.CId = '01');
--自己的答案

SELECT a.Sid, b.Sid, st1.* FROM 
(SELECT sc2.Sid FROM score sc2 WHERE sc2.Cid = 02) a
INNER JOIN (SELECT sc1.Sid FROM score sc1 WHERE sc1.Cid != 01
GROUP BY sc1.Sid ) b
ON a.Sid = b.Sid
INNER JOIN student st1 ON st1.Sid = a.Sid

-- 自己的答案2
select *
from score sc1
inner join score sc2 on sc1.Sid= sc2.Sid
where sc1.Cid != 01  and sc2.Cid = 02
group by sc1.Sid;








-- 2.查询平均成绩大于等于 60 分的同学的学生编号和学生姓名和平均成绩
SELECT st.SId, Sname, AVG(score) 'avg' FROM student st JOIN sc s ON st.SId = s.SId GROUP BY st.SId HAVING avg >= 60;
-- 自己的答案

select st.*, avg(sc.score) as avgscore
from score sc
inner join student st on sc.Sid = st.Sid
group by sc.Sid
having  avgscore >= 60;




-- 3.查询在 SC 表存在成绩的学生信息
SELECT * FROM student WHERE SId IN
(SELECT DISTINCT SId FROM sc);
--自己的答案
select st.*, sc.*
from score sc
inner join student st on st.Sid = sc.Sid
group by sc.Sid;




-- 4.查询所有同学的学生编号、学生姓名、选课总数、所有课程的成绩总和
SELECT st.sid, st.Sname, COUNT(sc.CId) 'nums', SUM(sc.score) 'sum' FROM
student st JOIN sc on st.SId = sc.SId
GROUP BY st.SId;
--自己的答案

select st.Sid, st.Sname, count(sc.score) as countscore, sum(sc.score) as sumscore, avg(sc.score) as avgscore
from score  sc
inner join student st on sc.Sid = st.Sid
group by  sc.Sid;



-- 4.1显示没选课的学生(显示为NULL)
SELECT st.sid, st.Sname, CASE WHEN COUNT(sc.SId) > 0 THEN COUNT(sc.SId) ELSE NULL END 'nums', SUM(sc.score) 'sum' FROM
student st left JOIN sc on st.SId = sc.SId
GROUP BY st.SId;

-- 4.2查有成绩的学生信息
SELECT * FROM student WHERE SId IN (SELECT DISTINCT SId FROM sc); -- 适用于右表小
SELECT * FROM student st WHERE EXISTS (SELECT * FROM sc WHERE sc.SId = st.SId); -- 适用于右表大


-- 5.查询「李」姓老师的数量
SELECT COUNT(*) FROM teacher t WHERE t.Tname LIKE '李%';

-- 6.查询学过「张三」老师授课的同学的信息
SELECT * FROM student st WHERE st.SId IN (
SELECT SId from sc WHERE sc.CId = (
SELECT CId FROM course WHERE course.TId = (
SELECT TId FROM teacher WHERE teacher.Tname = '张三')));

-- 7.查询没有学全所有课程的同学的信息
SELECT * FROM student WHERE SId NOT IN(
SELECT SId FROM sc GROUP BY SId HAVING COUNT(*) = (SELECT COUNT(*) FROM course));

-- 8.查询至少有一门课与学号为" 01 "的同学所学相同的同学的信息
SELECT * FROM student WHERE SId IN (SELECT SId FROM sc WHERE CId IN (SELECT CId FROM sc WHERE SId = '01'));
--myasr

select st.*
from student st
inner join score sc on st.Sid = sc.Sid
where  sc.Cid IN (select Cid from score where Sid = '01')
group by Sid;




-- 9.查询和" 01 "号的同学学习的课程完全相同的其他同学的信息
-- 解法一
SELECT * FROM student WHERE SId IN
(SELECT SId FROM sc GROUP BY SId HAVING SId <> '01' AND GROUP_CONCAT(CId ORDER BY CId) = 
(SELECT GROUP_CONCAT(CId ORDER BY CId) FROM sc WHERE SId = '01'));
-- 解法二
SELECT * FROM student WHERE SId IN
(SELECT SId FROM sc WHERE CId IN (SELECT CId FROM sc WHERE SId = '01') AND SId <> '01' 
GROUP BY SId 
HAVING COUNT(*) = (SELECT COUNT(*) FROM sc WHERE SId = '01'));

-- 10.查询没学过"张三"老师讲授的任一门课程的学生姓名
SELECT student.Sname FROM student WHERE SId NOT IN
(SELECT SId FROM sc WHERE CId in 
(SELECT course.CId FROM course WHERE TId =
(SELECT teacher.TId FROM teacher WHERE tname = '张三')));

--myasr
select *
from student st
left join 


(select
st.Sid
from
student st
inner join score sc on st.Sid = sc.Sid
inner join course co on sc.Cid = co.Cid
inner join teacher te on te.Tid = co.Tid
where te.Tname = '张三'
)  aa
on  aa.Sid = st.Sid
where aa.Sid is null;



-- 11.查询两门及其以上不及格课程的同学的学号，姓名及其平均成绩
SELECT st.SId, sname, avg(score) FROM student st JOIN sc ON st.sid = sc.SId GROUP BY st.SId 
HAVING COUNT(score <= 60 OR NULL) >= 2;
-- 错误解法：
select student.sid, student.sname, AVG(sc.score) from student,sc
where 
    student.sid = sc.sid and sc.score< 60
group by sc.sid 
having count(*)>1;

-- 12.检索" 01 "课程分数小于 60，按分数降序排列的学生信息
SELECT st.*, score
    FROM student st JOIN sc 
        ON st.SId = sc.SId AND sc.CId = '01' AND sc.score <  60
            ORDER BY score DESC;

-- 13.按平均成绩从高到低显示所有学生的所有课程的成绩以及平均成绩
SELECT * from sc JOIN 
(SELECT SId, avg(score) avg FROM sc GROUP BY SId) avgs
ON sc.SId = avgs.SId
ORDER BY avgs.avg desc;

-- 14.查询各科成绩最高分、最低分和平均分
-- 以如下形式显示：课程 ID，课程 name，最高分，最低分，平均分，及格率，中等率，优良率，优秀率
-- 及格为>=60，中等为：70-80，优良为：80-90，优秀为：>=90
-- 要求输出课程号和选修人数，查询结果按人数降序排列，若人数相同，按课程号升序排列
SELECT 
sc.CId '课程 ID',
count(*) '选修人数',
course.Cname '课程 name',
MAX(score) '最高分', 
MIN(score) '最低分', 
AVG(score) '平均分',
COUNT(score >= 60 OR NULL) / COUNT(*) '及格率',
COUNT(score >= 70 AND score < 80 OR NULL) / COUNT(*) '中等率',
COUNT(score >= 80 AND score < 90 OR NULL) / COUNT(*) '优良率',
COUNT(score >= 90 OR NULL) / COUNT(*) '优秀率'
FROM sc join course ON sc.CId = course.CId GROUP BY sc.CId
ORDER BY COUNT(*) desc, sc.CId;

-- 15.按各科成绩进行排序，并显示排名， Score 重复时保留名次空缺
SELECT CId, SId, score , (SELECT COUNT(*) FROM sc WHERE CId = o.CId AND score > o.score) + 1 rank
FROM sc o ORDER BY CId, score DESC;

-- 16.查询学生的总成绩，并进行排名，总分重复时不保留名次空缺
SET @rank = 0;
SELECT q.sid, q.sum, (@rank := @rank + 1) rank FROM
(SELECT sid, sum(score) sum  FROM sc GROUP BY SId ORDER BY sum) q;

-- 17.统计各科成绩各分数段人数：课程编号，课程名称，[100-85]，[85-70]，[70-60]，[60-0] 及所占百分比
SELECT s.CId, c.Cname,
CONCAT(COUNT(score >= 85 OR NULL), ', ',COUNT(score >= 85 OR NULL) / COUNT(*)) '[100-85]',
COUNT(score < 85 AND score >= 70 OR NULL) '[85-70]',
COUNT(score < 70 AND score >= 60 OR NULL) '[70-60]',
COUNT(score < 60 OR NULL) '[60-0]'
FROM sc s JOIN course c ON s.CId = c.CId GROUP BY s.CId;

-- 18. 查询各科成绩前三名的记录
SELECT o.* FROM sc o HAVING 
(SELECT COUNT(*) FROM sc WHERE o.CId = CId AND o.score < score) + 1 <= 3 
ORDER BY CId, score DESC, SId;

-- 19.查询每门课程被选修的学生数
SELECT CId, COUNT(*) FROM sc GROUP BY CId;

-- 20.查询出只选修两门课程的学生学号和姓名
SELECT st.SId, st.Sname
FROM student st JOIN sc s ON st.SId = s.SId GROUP BY st.SId HAVING COUNT(*) = 2;

-- 21.查询男生、女生人数
select ssex, count(*) from student
group by ssex;

-- 22.查询名字中含有「风」字的学生信息
SELECT * FROM student WHERE Sname LIKE '%风%';

-- 23.查询同名学生名单，并统计同名人数
SELECT st.*, (SELECT COUNT(*) FROM student WHERE Sname = st.Sname) 'same' FROM student st WHERE st.SId IN
(SELECT s1.SId FROM student s1 JOIN student s2 ON s1.SId != s2.SId AND s1.Sname = s2.Sname);

-- 24.查询 1990 年出生的学生名单
SELECT * FROM student WHERE YEAR(Sage) = 1990;

-- 25.查询每门课程的平均成绩，结果按平均成绩降序排列，平均成绩相同时，按课程编号升序排列
SELECT CId, avg(score) avg
FROM sc GROUP BY CId ORDER BY avg DESC, CId;

-- 26.查询平均成绩大于等于 85 的所有学生的学号、姓名和平均成绩
SELECT st.SId, Sname, AVG(score) avg
FROM student st JOIN sc s ON st.SId = s.SId GROUP BY st.SId HAVING avg >= 85;

-- 27.查询课程名称为「数学」，且分数低于 60 的学生姓名和分数
SELECT st.Sname, s.score
FROM student st, sc s, course c
WHERE st.SId = s.SId AND s.CId = c.CId AND s.score < 60 AND c.Cname = '数学';

-- 28.查询所有学生的课程及分数情况（存在学生没成绩，没选课的情况）
SELECT Sname, st.SId, score 
FROM student st LEFT JOIN sc s ON st.SId = s.SId;

-- 29.查询任何一门课程成绩在 70 分以上的姓名、课程名称和分数
-- 这个我没太理解，理解一是任意一门成绩均在70分以上
SELECT st.Sname, c.Cname, s.score
FROM student st, sc s, course c WHERE st.SId = s.SId AND s.cid = c.cid AND st.SId IN
(SELECT sid FROM sc GROUP BY SId HAVING MIN(score) > 70);
-- 理解二是存在一门成绩在70分以上即可满足条件
SELECT st.Sname, c.Cname, s.score
FROM student st, sc s, course c WHERE st.SId = s.SId AND s.cid = c.cid AND st.SId IN
(SELECT sid FROM sc GROUP BY SId HAVING MAX(score) > 70);
-- 理解三就是找出所有大于70分的得分。
select student.sname, course.cname,sc.score from student,course,sc
where sc.score>70
and student.sid = sc.sid
and sc.cid = course.cid;
SELECT * FROM sc WHERE score > 70

-- 30.查询存在不及格的课程
SELECT cid FROM sc GROUP BY CId HAVING MIN(score) < 60;
select cid from sc
where score< 60
group by cid;
SELECT DISTINCT cid FROM sc WHERE score < 60;

-- 31.查询课程编号为 01 且课程成绩在 80 分及以上的学生的学号和姓名
SELECT sid, sname FROM student WHERE sid IN
(SELECT SId FROM sc WHERE CId = '01' AND score >= 80);

-- 32.求每门课程的学生人数
SELECT cid, count(*) FROM sc GROUP BY cid;

-- 33.成绩不重复，查询选修「张三」老师所授课程的学生中，成绩最高的学生信息及其成绩
SELECT st.*, score FROM student st JOIN sc s ON st.SId = s.SId AND s.CId =
(SELECT CId FROM course WHERE TId =
(SELECT TId FROM teacher WHERE Tname = '张三')) ORDER BY score DESC LIMIT 1;

-- 34.成绩有重复的情况下，查询选修「张三」老师所授课程的学生中，成绩最高的学生信息及其成绩
SELECT st.*, score, CId FROM student st JOIN sc s ON st.SId = s.SId AND s.CId = 
(SELECT CId FROM course WHERE TId =
(SELECT TId FROM teacher WHERE Tname = '张三'))
WHERE score = (SELECT max(score) FROM sc WHERE CId = s.CId);

-- 35.查询不同课程成绩相同的学生的学生编号、课程编号、学生成绩
-- 这个问题其实一开始没太明白啥意思，后来理解为某个人的几科分数是一样的，需要把这个人找出来
select  a.cid, a.sid,  a.score from sc as a
inner join 
sc as b
on a.sid = b.sid
and a.cid != b.cid
and a.score = b.score
group by cid, sid;

-- 36.查询每门功成绩最好的前两名
SELECT o.* FROM sc o HAVING (SELECT COUNT(*) FROM sc WHERE CId = o.CId AND score > o.score) + 1 <= 2  ORDER BY o.cid, sid;

-- 37.统计每门课程的学生选修人数（超过 5 人的课程才统计）
SELECT CId, COUNT(*) sum FROM sc GROUP BY CId HAVING sum > 5;

-- 38.检索至少选修两门课程的学生学号
SELECT SId FROM sc GROUP BY SId HAVING COUNT(*) >= 2;

-- 39.查询选修了全部课程的学生信息
SELECT * FROM student WHERE sid IN
(SELECT SId FROM sc GROUP BY SId HAVING COUNT(*) = (SELECT COUNT(*) FROM course)); 

-- 40.查询各学生的年龄，只按年份来算
SELECT SId, Sname, Sage, YEAR(NOW()) - YEAR(Sage)
FROM student;

-- 41.按照出生日期来算，当前月日 < 出生年月的月日则，年龄减一
SELECT SId '学生编号', Sname '学生姓名', TIMESTAMPDIFF(YEAR, Sage, NOW()) '学生年龄'
FROM student;

-- 42.查询本周过生日的学生
-- 有点复杂，需要拼接出本周的起止日期
SELECT * FROM student 
WHERE DATE(CONCAT(YEAR(NOW()),'-',MONTH(Sage), '-', DAY(Sage)))
BETWEEN DATE(DATE_SUB(NOW(),INTERVAL WEEKDAY(NOW()) DAY)) 
AND DATE(DATE_ADD(NOW(),INTERVAL 6 - WEEKDAY(NOW()) DAY));
SELECT * FROM student 

-- 43. 查询下周过生日的学生
-- 同42
SELECT * FROM student 
WHERE DATE(CONCAT(YEAR(NOW()),'-',MONTH(Sage), '-', DAY(Sage)))
BETWEEN DATE(DATE_SUB(NOW(),INTERVAL WEEKDAY(NOW()) - 7 DAY)) 
AND DATE(DATE_ADD(NOW(),INTERVAL 13 - WEEKDAY(NOW()) DAY));

-- 44.查询本月过生日的学生
SELECT * FROM student WHERE month(sage) = month(NOW())

-- 45.查询下月过生日的学生
-- 注意本月是12月的话，下一个月份是1即可
SELECT * FROM student WHERE month(Sage) = (CASE WHEN month(NOW()) = 12 THEN 1 ELSE MONTH(NOW()) + 1 END);
