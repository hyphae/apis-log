# apis-log

## Introduction

apis-logはapis-main等がコミュニケーションラインにマルチキャストで出力する情報を受信し  
JSON形式に整えた上でデータベースに書き込みを行うソフトウェアである。

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
$ cd ../
$ mkdir apis-log_exe
$ cp ./apis-log/target/apis-log-*-fat.jar ./apis-log_exe
$ cp ./apis-log/setting_files/* ./apis-log_exe
```

## Parameter Setting
Set the following file parameters in the apis-log_exe at least to suit your environment.   
Refer to "Chapter 7, About Configuration Files" in the [apis-log_specification](#anchor1) for more information.

&emsp;start.sh  
&emsp;&emsp;&emsp;- java arguments &emsp;(default : 127.0.0.1) 


## Running

```bash
$ cd apis-log_exe
$ bash start.sh
```

<a id="anchor1"></a>
## Documentation



## License
&emsp;[Apache License Version 2.0](https://github.com/SonyCSL/apis-log/blob/master/LICENSE)


## Notice
&emsp;[Notice](https://github.com/SonyCSL/apis-log/blob/master/NOTICE.md)
