
## 如何阅读源码

> 通过清华镜像来下载源码(AOSP)，源码地址

- git clone https://aosp.tuna.tsinghua.edu.cn/platform/frameworks/base 

- git clone https://aosp.tuna.tsinghua.edu.cn/platform/libcore 

> clone的时候会报错，原因是windows上无法checkout带有?的文件。可以不用管

- git clone https://aosp.tuna.tsinghua.edu.cn/platform/system/core 

- git clone https://aosp.tuna.tsinghua.edu.cn/platform/art 

> 也可以通过github的[镜像下载](https://github.com/aosp-mirror)


####tips

- 单独下载的同时使用git的--depth 1来帮助减少下载量

- 由于文件较大，可能需要设置 git config --global http.postBuffer 524288000


#### insight-source快捷键







