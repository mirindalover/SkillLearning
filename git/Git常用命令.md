# Git 常用命令

## 新建：

- 下载整个项目
> git clone [url]

- 新建一个分支
> git branch [branch-name]

- 新建一个分支，并切换到分支
> git checkout -b [branch-name]
相当于 git branch [branch-name] ———— git checkout [branch-name]

- 添加文件到缓存区
> git add [file1] [file2] ... //添加文件
> git add [dir] //添加目录
> git add . //添加当前目录的所有文件(不包括删除文件)
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
> git config  --global user.name 你的目标用户名；
> git config  --global user.email 你的目标邮箱名;

- 显示有变更的内容
> git status


