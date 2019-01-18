# gimbap [![Build Status](https://travis-ci.org/wowselim/gimbap.svg?branch=master)](https://travis-ci.org/wowselim/gimbap)

Disk-backed binary storage that supports all CRUD operations.
Optional cloud extensions for aliyun-oss or s3 are available.
All s3 compatible object stores should be supported with the latter.

## Using gimbap

First, add gimbap to your dependencies using the [jitpack repository](https://jitpack.io/#wowselim/gimbap).

### For small objects
```java
Store<byte[]> binaryStore = new BinaryStore();

String id = binaryStore.put("gimbap".getBytes());
byte[] data = binaryStore.get(id);

System.out.println(new String(data));
// prints gimbap
```

### For large objects
```java
StreamingStore<byte[]> binaryStore = new BinaryStore();

String id = binaryStore.putStream(
        new ByteArrayInputStream("gimbap".getBytes()));
byte[] data = binaryStore.get(id);

System.out.println(new String(data));
// prints gimbap
```

### Using Alibaba Cloud Object Storage Service (streaming supported)
**Add version 3.4.0 of the [OSS SDK](https://github.com/aliyun/aliyun-oss-java-sdk)
to your dependencies.**

```java
StreamingStore<byte[]> binaryStore = new OSSStore("endpoint",
        "bucketName",
        "accessKeyId",
        "accessKeySecret");

String id = binaryStore.put("gimbap".getBytes());
byte[] data = binaryStore.get(id);

System.out.println(new String(data));
// prints gimbap
```
