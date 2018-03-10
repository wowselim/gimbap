# gimbap [![Build Status](https://travis-ci.org/wowselim/gimbap.svg?branch=master)](https://travis-ci.org/wowselim/gimbap)

Disk-backed binary storage

## Using gimbap

```java
Store<byte[]> binaryStore = new BinaryStore();

String id = binaryStore.put("gimbap".getBytes());
byte[] data = binaryStore.get(id);

System.out.println(new String(data));
// prints gimbap
```