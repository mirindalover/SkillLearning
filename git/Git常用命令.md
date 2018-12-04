# Git 常用命令

## 新建：

- 下载整个项目
> git clone [url]

-	下载指定分支的项目
>	git clone -b [分支] [url]

- 新建一个分支
> git branch [branch-name]

- 新建一个分支，并切换到分支
> git checkout -b [branch-name]

相当于 

- 新建分支
> git branch [branch-name] 


- 切换分支
> git checkout [branch-name]

- 添加文件到缓存区
> git add [file1] [file2] ... //添加文件
>
> git add [dir] //添加目录
>
> git add . //添加当前目录的所有文件(不包括删除文件)
>
> git add -A //添加当前目录的所有文件

## 提交

- 提交暂存区到仓库区
> git commit -m [message]

- 提交暂存区的指定文件到仓库区
> $ git commit [file1] [file2] ... -m [message]

- 推送到远程仓库
> git push [remote] [branch]
		
## 属性查看：

- 查看当前git的地址
> git remote -v

- 获取用户名和邮箱
> git config --list

> git config --local user.name	查看本地仓库的用户名

> git config  --global user.name 你的目标用户名；
>
> git config  --global user.email 你的目标邮箱名;

- 显示有变更的内容
> git status

- 查看日志
> git log

- 查看历史日志(切换到了以前的版本)
> git reflog(英文q退出)


## 切换

- 查看远程分支
> git branch -a

- 查看本地分支
> git branch

- 版本回滚
> git revert

- 回滚到上个版本
> git reset --hard HEAD^
> git reset --hard HEAD^^ 上上版本
> git reset --hard HEAD~100 上100个版本
> git reset --hard [commit_id]

- 删除本地缓存，不让该文件受版本的管理
> git rm -r --cached [文件名称]
> 应用示例:gitignore写错导致文件add到缓存

- 删除远程文件a
>  git rm -r --cached a

## 合并

-	合并分支
>	git merge test  在当前分支合并test分支内容
>	git status		查看冲突
>	git add . 		修改后提交

## 恢复

-	恢复一个没有提交的文件
>	git checkout [文件]

## 设置：

- 设置name
> git config --global user.name [userName]

> git config --local user.name [userName]	设置本地用户

- 设置email
> git config --global user.email [userEmail]


## 创建git仓库与本地关联

    1. 本地创建文件

    2.  git init 初始化本地git仓库
    
    3. git config 配置本地用户和email
    
    4. git add . git commit 
    
    5. git remote add origin [http://]
    
    6. git push origin master


## git解决冲突：

	git pull  ：
		出现error: Your local changes to the following files would be overwritten by merge:
	git add.
	git commit (添加到本地库后)
	git pull(此时，文件是冲突后的文件，我们需要手动删除来解决冲突)
	再次执行上传流程，即可
	
	二、tortoiseGit --> Edit conflict
	把最终的结果，保存到merge中

##	studio刚导入没有关联git，不显示提交的问题

>	setting中version control来手动添加


	
git 错误:error: RPC failed; HTTP 411 curl 22 The requested URL returned error: 411 Length Required

出现这个错误的原因是git 通过http post的大小有限制，应该调大一些就可以了

>	git config http.postBuffer  524288000
	
	
	