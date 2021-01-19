# apis-log

## Introduction

apis-log is software for receiving data from apis-main by multicast via a communication line and storing that information in a database in JSON format.

![キャプチャ](https://user-images.githubusercontent.com/71874910/95825383-bb4d5000-0d6b-11eb-97ab-c0012842111c.PNG)


## Installation
Here is how to install apis-log individually.  
git, maven, groovy and JDK must be installed in advance.

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

## Running

Here is how to run apis-log individually.  

```bash
$ cd exe
$ bash start.sh
```

## Stopping

Here is how to stop apis-log individually.  

```bash
$ cd exe
$ bash stop.sh
```

## Parameter Setting
Set the following parameters in the exe folder as necessary.   
Refer to "Chapter 6, Configuration Files" in the [apis-log_specification](#anchor1) for more information.

&emsp;config.json   
&emsp;&emsp;&emsp;- communityId   &emsp;(default : oss_communityId)  
&emsp;&emsp;&emsp;- clusterId     &emsp;(default : oss_clusterId)  

&emsp;start.sh  
&emsp;&emsp;&emsp;-conf &emsp; (default : ./config.json)  
&emsp;&emsp;&emsp;-cluster-host &emsp; (default : 127.0.0.1)  



<a id="anchor1"></a>
## Documentation
&emsp;[apis-mian_log_specification(EN)](https://github.com/SonyCSL/apis-log/blob/main/doc/en/apis-log_specification_EN.md)  
&emsp;[apis-mian_log_specification(JP)](https://github.com/SonyCSL/apis-log/blob/main/doc/jp/apis-log_specification_JP.md)

## API Specification  

An example of creating an API specification using the Javadoc command is shown below.  
(For Ubuntu18.04)  
  
```bash  
$ export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/  
$ cd apis-log  
$ mvn javadoc:javadoc  
```  

The API specification is created in apis-log/target/site/apidocs/.  


## License
&emsp;[Apache License Version 2.0](https://github.com/SonyCSL/apis-log/blob/master/LICENSE)


## Notice
&emsp;[Notice](https://github.com/SonyCSL/apis-log/blob/master/NOTICE.md)
