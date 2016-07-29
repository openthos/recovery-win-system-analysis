# Windows-Recovery需求与设计实现文档

- 项目简介
- 功能需求
- 项目进展
- 设计实现
- 存在问题

# 项目简介

本项目属于openthos项目的一部分，在openthos系统中恢复windows系统

## 功能需求

* 文件检查功能
  * [x] 检查文件是否存在
  * [x] 根据SHA-1码检验文件完整性
* [x] 文件下载功能
* [x] 网络检测功能
* [x] 硬盘分区显示功能
* [x] 系统部署功能
* [x] 恢复完成后重启功能
 

# 项目进展

<table>
<tr>
<th>人员</th>
<th>开始时间 </th>
<th>结束时间 </th>
<th>内容</th>
</tr>


<tr>
<td rowspan=07>薛海龙</td>
</tr>

<tr>
<td>2016-04-20</td>
<td>2016-04-29</td>
<td>熟悉整个App的运作流程，学习App的布局及功能实现代码</td>
</tr>

<tr>
<td>2016-05-02</td>
<td>2016-05-13</td>
<td>修改界面，改为左右两侧分屏模式，本地提供win7，win8，win10的wim文件，云端提供win7家庭版、企业版，win8，win10的下载地址</td>
</tr>

<tr>
<td>2016-06-06</td>
<td>2016-06-10</td>
<td>完成之前App运行到openthos上就崩溃的Bug（getconfig方法的数组越界问题）</td>
</tr>


<tr>
<td>2016-06-13</td>
<td>2016-06-17</td>
<td>解决了wimlib-imgex无法拷贝到system/app目录下的问题（U盘启动的openthos系统没有写入权限），并制作wim文件进行测试</td>
</tr>

<tr>
<td>2016-06-20</td>
<td>2016-07-01</td>
<td>修改界面，并解决之前测试一直出错的问题（wimlib-imgex拷贝的目录、wim文件制作的过程）</td>
</tr>


<tr>
<td>2016-07-04</td>
<td>2016-07-15</td>
<td>根据不同的windows系统测试App</td>
</tr>


</table>


# 设计实现

[设计详情]()

[测试过程参考](https://github.com/openthos/recovery-win-system/pull/8)


# 存在问题


   * 下载wim文件速度很慢
   * 硬盘分区列表没有明确列出win系统分区



