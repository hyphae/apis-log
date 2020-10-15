# apis-log

## Introduction

apis-logはapis-main等がコミュニケーションラインにマルチキャストで出力する情報を受信しJSON形式に整えた上で  
データベースに書き込みを行うソフトウェアである。

![キャプチャ](https://user-images.githubusercontent.com/71874910/95825383-bb4d5000-0d6b-11eb-97ab-c0012842111c.PNG)


## Installation
```bash
$ git clone https://github.com/SonyCSL/apis-bom.git
$ cd apis-bom
$ mvn install
$ cd ../
$ git clone https://github.com/SonyCSL/apis-common.git
$ cd apis-common
$ mvn install
$ cd ../
$ git cone https://github.com/SonyCSL/apis-log.git
$ cd apis-log
$ mvn package
```

## Parameter Setting
Set the following file parameters in the apis-log_exe at least to suit your environment.   
Refer to "Chapter 6, About Configuration Files" in the [apis-log_specification](#anchor1) for more information.

&emsp;config.json   
&emsp;&emsp;&emsp;- communityId   &emsp;(default : oss_communityId)  
&emsp;&emsp;&emsp;- clusterId     &emsp;(default : oss_clusterId)  

&emsp;start.sh  
&emsp;&emsp;&emsp;-conf &emsp; (default : ./config.json)  
&emsp;&emsp;&emsp;-cluster-host &emsp; (default : 127.0.0.1)  



## Running

```bash
$ cd exe
$ bash start.sh
```

<a id="anchor1"></a>
## Documentation



## License
&emsp;[Apache License Version 2.0](https://github.com/SonyCSL/apis-log/blob/master/LICENSE)


## Notice
&emsp;[Notice](https://github.com/SonyCSL/apis-log/blob/master/NOTICE.md)
