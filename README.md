# 说明

1 这个代码通过JNI实现BLS12-381的签名聚合

2 依赖 https://github.com/herumi/mcl

3 编译 mvn install

4 运行 java -jar -Djava.library.path=. JavaBLS-1.0-SNAPSHOT.jar

5 运行结果

libName : libmcljava.so  
curve=BLS12_381  
提取公钥耗时 16  
签名耗时 2  
OK : verify signature  
OK : verify signature  
OK : verify signature  
OK : verify signature  
OK : verify signature  
OK : verify signature  
OK : verify signature  
OK : verify signature  
OK : verify signature  
OK : verify signature  
单个验证签名耗时 22  
OK : verify signature  
聚合验证签名耗时 2  
