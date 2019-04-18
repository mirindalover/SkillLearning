# Git 常用命令

## 新建：

- 下载整个项目
> git clone [url]

- 下载指定分支的项目
> git clone -b [分支] [url]

- 深度下载(指定最近 commit)
> git clone [url] --depth 1  也可指定分支

- 新建一个分支
> git branch [branch-name]

- 新建一个分支，并切换到分支
> git checkout -b [branch-name]
> git checkout -b totallyNotMaster origin/master 可以指定分支跟踪的远程分支(提交和更新都与远程分支保持)

> git branch -u o/master foo  把分支关联到 远程分支

相当于 

- 新建分支
> git branch [branch-name] 
> git branch HEAD^^2^ 链式调用，在以前的提交历史新建分支
> git branch [branch-name] [commitID] 基于某个提交记录新建分支

- 切换分支
> git checkout [branch-name]

- 添加文件到缓存区
> git add [file1] [file2] ... //添加文件
>
> git add [dir] //添加目录
>
> git add . //添加当前目录的所有新建、修改文件(不包括删除文件)
>
> git add -u //添加当前目录的所有删除、修改文件(不包括删除文件)
>
> git add -A //添加当前目录的所有文件

## 更新

- fetch
> git fetch 下载远程代码的更新到本地，但不会修改本地代码
> git fetch origin <source>:<destination>  与pull类似，拉取远程的分支到本地
> 当source时空时，相当于创建一个本地分支

- pull
> git pull 相当于 git fetch ,git merge 下载合并
> git pull --rebase 相当于 git fetch ,git rebase 
> git pull origin <source>:<destination>,相当于fetch source...,merge

## 提交

- 提交暂存区到仓库区
> git commit -m [message]

> git commit --amend [message] 修改最近提交(直接修改文件后 add+ commit --amend)

- 提交暂存区的指定文件到仓库区
> $ git commit [file1] [file2] ... -m [message]

- 推送到远程仓库
> git push [remote] [branch]   指定分支推送到远程，不需要HEAD必须在当前
> git push origin <source>:<destination>   source可以是dev、dev^。destination即是分支，没有的话会创建
> 当source是空时，相当于删除远程分支
> git push默认使用的是config.default的设置模式		

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

- 把分支移动某个提交
> git branch -f master HEAD^  吧master分支强制移动到HEAD^提交处

- 删除分支
> git branch -d <branch_name>
> git push origin --delete <branch_name>  删除远程仓库的分支

- 版本回滚
> git revert  与reset不同的是，revert会新建一个修改版本
> 则，revert是修改提交，reset只是本地回滚

- 回滚到上个版本
> git reset --hard HEAD^
> git reset --hard HEAD^^ 上上版本
> git reset --hard HEAD~100 上100个版本
> git reset --hard [commit_id]

- 删除本地缓存，不让该文件受版本的管理
> git rm -r --cached [文件名称]
> 应用示例:gitignore写错导致文件add到缓存
> git rm --cached *xxx 忽略文件(可与gitigonre相同)

- 忽略文件，不受版本控制
> git update-index --assume-unchanged PATH  只能是文件
> cd dir    ls | xargs -l git update-index --assume-unchanged 可以忽略文件夹
> 应用于已经加入了版本控制，但是本地不想接受控制

- 删除远程文件a
>  git rm -r --cached a
>  git rm -r --cached *.iml



- 移动Head
>	git checkout  [cimmit_id]  由原来的Head->master->id变成了Head->id
> 	git checkout HEAD~^2~2   链式调用，先移动到前一个，再移动到head前第二个父节点(合并过来的有2个节点),再移动前2个节点

## 合并

-	合并分支
>	git merge test  在当前分支合并test分支内容
>	git status		查看冲突
>	git add . 		修改后提交

>   git merge --no-commit  merge但不提交

- 	rebase
>	git rebase master dev 把dev分支合并到到master分支
>   此时只是合并，master还没有在最前面。还需要命令 git rebase dev master
>	git rebase -i id  使用图形化的工具来合并 可排序提交

> 	merge和rebase的区别,rebase使提交树清晰，但是改变了顺序。merge则保留着顺序

- cherry-pick
> git cherry-pick c1 c2 c3 把提交id c1 c2 c3 添加到 master分支

## 恢复

-	恢复一个没有提交的文件
>	git checkout [文件]

## 设置：

- 设置name
> git config --global user.name [userName]

> git config --local user.name [userName]	设置本地用户

- 设置email
> git config --global user.email [userEmail]

## TAG

- 在某个提交id打TAG
> git tag [TAG] [commit_id] -m [MESSAGE] ,-m 是注释，可不写
> git push origin [TAG_NAME] 把tag提交到远程,相当于git push origin refs/tags/[TAG]:refs/tags/[TAG]

- 查找TAG
> git describe  只会列出有注释的tag
> git tag 		查看所有tag

- 删除tag
> git tag -d [TAG]
> git push origin :refs/tags/[TAG]

## Submodules命令

- 添加模块
> git submodule add [模块] [文件夹]

可看到添加了.gitmodules文件

- clone带有submodule的项目
> git submodule 可以查看submodle的状态  (-表示还没有检出)

> git submodule init
> git submodule update

.git/config后面有submodule的信息

- 修改依赖的模块

> cd [module]
> git checkout master
> git commit -m 
> git push

- 同时提交项目的依赖模块id
> cd app
> git commit -m 
> git push

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
	
## git分支开发时，某一个提交需要合并到dev中

	{没有提交记录
	git commit   需要合并的内容
	
	git push origin target:target 	如果没有创建分支会创建
	
	git commit   提交不需要合并的内容
	
	git checkout dev
	
	git merge origin/target   合并远程的target分支
	}
	
	{有提交记录
	git checkout dev
	
	git cherry-pick [需要提交的id]
	
	git push
	}	
	
	git checkout target  可以继续开发

##  git版本库迁移

	git remote set-url origin [url] 改变git的url即可
	
	后续直接提交

##	studio刚导入没有关联git，不显示提交的问题

>	setting中version control来手动添加

## git使用本地的仓库来管理远程的仓库(可以到达本地多个 push到本地的仓库，本地仓库再同步到远程)

> git clone
> git config receive.denyCurrentBranch ignore
> git config --bool receive.denyNonFastForwards false

创建本地其他副本
> git clone [本地文件地址]
> git push    推送到本地仓库

本地仓库
> git push origin dev:dev  把本地仓库更新到远程

	
git 错误:error: RPC failed; HTTP 411 curl 22 The requested URL returned error: 411 Length Required

或者：error: RPC failed; curl 18 transfer closed with outstanding read data remaining

出现这个错误的原因是git 通过http post的大小有限制，应该调大一些就可以了

> git config http.postBuffer  524288000

> git config --global http.postBuffer 524288000
	
## Git说明
	
git分支：不会创建多余的内存，只是指向某个提交记录。
	
	
	
	
	
	
	