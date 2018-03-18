# gimbap [![Build Status](https://travis-ci.org/wowselim/gimbap.svg?branch=master)](https://travis-ci.org/wowselim/gimbap)

Disk-backed binary storage that supports all CRUD operations.

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
