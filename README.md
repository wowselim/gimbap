# gimbap [![Build Status](https://travis-ci.org/wowselim/gimbap.svg?branch=master)](https://travis-ci.org/wowselim/gimbap)

Disk-backed binary storage that supports all CRUD operations.
Optional cloud extensions are available.

## Using gimbap

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
**Add version 3.1.0 of the [OSS SDK](https://github.com/aliyun/aliyun-oss-java-sdk)
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
