##Guava -Caache
[ Guava缓存值CacheBuilder介绍-参考](http://blog.csdn.net/xlgen157387/article/details/47293517)
[Google -CachesExplained wiki](https://github.com/google/guava/wiki/CachesExplained)
###概述
缓存是日常开发中经常应用到的一种技术手段，合理的利用缓存可以极大的改善应用程序的性能。
Guava官方对Cache的描述[连接](https://github.com/google/guava/wiki/CachesExplained)
缓存在各种各样的用例中非常有用。例如，当计算或检索值很昂贵时，您应该考虑使用缓存，并且不止一次需要它在某个输入上的值。
缓存ConcurrentMap要小，但不完全相同。最根本的区别在于一个ConcurrentMap坚持所有添加到它直到他们明确地删除元素。
另一方面，缓存一般配置为自动退出的条目，以限制其内存占用。在某些情况下，一个LoadingCache可以即使不驱逐的条目是有用的，因为它的自动缓存加载。
（Caches are tremendously useful in a wide variety of use cases. For example, you should consider using caches when a value is expensive to compute or retrieve, and you will need its value on a certain input more than once.
 A Cache is similar to ConcurrentMap, but not quite the same. The most fundamental difference is that a ConcurrentMap persists all elements that are added to it until they are explicitly removed. A Cache on the other hand is generally configured to evict entries automatically, in order to constrain its memory footprint. In some cases a LoadingCache can be useful even if it doesn’t evict entries, due to its automatic cache loading.）
 
 ###适用性
 1. 你愿意花一些记忆来提高速度。You are willing to spend some memory to improve speed.
 2. 您希望Key有时会不止一次被查询。You expect that keys will sometimes get queried more than once.
 3. 你的缓存不需要存储更多的数据比什么都适合在。（Guava缓存是本地应用程序的一次运行）。Your cache will not need to store more data than what would fit inRAM. (Guava caches are local to a single run of your application. 
 4. 它们不将数据存储在文件中，也不存储在外部服务器上。如果这样做不适合您的需要，考虑一个工具像memcached。
 
 ###实战
 ####maven依赖
 ```
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>23.0</version>
</dependency>
```
###加载
在使用缓存前，首先问自己一个问题：有没有合理的默认方法来加载或计算与键关联的值？如果有的话，你应当使用CacheLoader。
如果没有，或者你想要覆盖默认的加载运算，同时保留”获取缓存-如果没有-则计算”[get-if-absent-compute]的原子语义，
你应该在调用get时传入一个Callable实例。缓存元素也可以通过Cache.put方法直接插入，但自动加载是首选的，
因为它可以更容易地推断所有缓存内容的一致性。自动加载就是createCacheLoader中的，当cache.get(key)不存在的时候，会主动的去加载值的信息并放进缓存中去。


###Guava Cache有以下两种创建方式：
####创建 CacheLoader
LoadingCache是附带CacheLoader构建而成的缓存实现。创建自己的CacheLoader通常只需要简单地实现V load(K key) throws Exception方法。例如，你可以用下面的代码构建LoadingCache：
CacheLoader: 当检索不存在的时候，会自动的加载信息的！
```
    public static com.google.common.cache.CacheLoader<String, Employee> createCacheLoader() {
        return new com.google.common.cache.CacheLoader<String, Employee>() {
            @Override
            public Employee load(String key) throws Exception {
                log.info("加载创建key:" + key);
                return new Employee(key, key + "dept", key + "id");
            }
        };
   }
  LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(30L, TimeUnit.MILLISECONDS)
                .build(createCacheLoader());
```
####创建 Callable
所有类型的Guava Cache，不管有没有自动加载功能，都支持get(K, Callable)方法。这个方法返回缓存中相应的值，或者用给定的Callable运算并把结果加入到缓存中。在整个加载方法完成前，缓存项相关的可观察状态都不会更改。这个方法简便地实现了模式”如果有缓存则返回；否则运算、缓存、然后返回”。
```
Cache<Key, Value> cache = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .build(); // look Ma, no CacheLoader
...
try {
  // If the key wasn't in the "easy to compute" group, we need to
  // do things the hard way.
  cache.get(key, new Callable<Value>() {
    @Override
    public Value call() throws AnyException {
      return doThingsTheHardWay(key);
    }
  });
} catch (ExecutionException e) {
  throw new OtherException(e.getCause());
}
```

###显式插入
使用cache.put(key, value)方法可以直接向缓存中插入值，这会直接覆盖掉给定键之前映射的值。使用Cache.asMap()视图提供的任何方法也能修改缓存。但请注意，asMap视图的任何方法都不能保证缓存项被原子地加载到缓存中。进一步说，asMap视图的原子运算在Guava Cache的原子加载范畴之外，所以相比于Cache.asMap().putIfAbsent(K,V)，Cache.get(K, Callable<V>) 应该总是优先使用。

###缓存回收
1. 大小
```
    @Test
    public void testSize() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.getUnchecked("wangji");
        cache.getUnchecked("wangwang");
        cache.getUnchecked("old wang");
        assertThat(cache.size(), equalTo(3L));

        cache.getUnchecked("new wang");
        Employee employee = cache.getIfPresent("wangji"); //不会重新加载创建cache
        log.info("最新的把老的替换掉：" + (employee == null ? "是的" : "否"));
        Employee newEmployee = cache.getIfPresent("new wang"); //不会重新加载创建cache
        log.info("获取结果：" + newEmployee);
    }
```
2. 权重（感觉用的比较少）不同的缓存项有不同的“权重”（weights）——例如，如果你的缓存值，占据完全不同的内存空间，你可以使用CacheBuilder.weigher(Weigher)指定一个权重函数，并且用CacheBuilder.maximumWeight(long)指定最大总重。在权重限定场景中，除了要注意回收也是在重量逼近限定值时就进行了，还要知道重量是在缓存创建时计算的，因此要考虑重量计算的复杂度
```
@Test
    public void testWeight() throws ExecutionException, InterruptedException {
        //如果不同的高速缓存条目有不同的“权重”，例如，如果你的缓存值有着完全不同的记忆的足迹--你可以用cachebuilder指定一个权重函数。秤（秤）和一个cachebuilder最大缓存量的最大重量（长）
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .maximumWeight(150)
                .weigher(new Weigher<String, Employee>() {
                    public int weigh(String key, Employee employee) {
                        int weight = employee.getName().length() + employee.getEmpID().length() + employee.getDept().length();//权重计算器
                        log.info("weight is :" + weight);
                        return weight;
                    }
                })
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.get("wangji");
        log.info("cacheSize：" + cache.size());
        cache.get("wangwang");
        log.info("cacheSize：" + cache.size());
        cache.get("old wang");
        log.info("cacheSize：" + cache.size());
        cache.get("new wang");
        log.info("cacheSize：" + cache.size());
    }
```
3. 定时回收（Timed Eviction）
Access time => Write/Update/Read
```
 /**
     * TTL->time to live
     * Access time => Write/Update/Read
     */
    @Test
    public void testEvictionByAccessTime() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterAccess(2, TimeUnit.SECONDS)
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.getUnchecked("wangji");
        TimeUnit.SECONDS.sleep(3);
        Employee employee = cache.getIfPresent("wangji"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));
        cache.getUnchecked("guava");

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

    }
```
 Write time => write/update
```
   /**
     * Write time => write/update
     */
    @Test
    public void testEvictionByWriteTime() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.getUnchecked("guava");
        TimeUnit.SECONDS.sleep(2);
        Employee employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        cache.put("guava", new Employee("guava", "guava" + "dept", "guava" + "id")); //手动插入
        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        cache.put("guava", new Employee("guava", "guava" + "dept", "guava" + "id"));
        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

    }
```
4. 基于引用的回收（Reference-based Eviction）[强（strong）、软（soft）、弱（weak）、虚（phantom）引用-参考](https://www.cnblogs.com/daxin/p/5604923.html)
通过使用弱引用的键、或弱引用的值、或软引用的值，Guava Cache可以把缓存设置为允许垃圾回收：
CacheBuilder.weakKeys()：使用弱引用存储键。当键没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（==），使用弱引用键的缓存用==而不是equals比较键。
CacheBuilder.weakValues()：使用弱引用存储值。当值没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（==），使用弱引用值的缓存用==而不是equals比较值。
CacheBuilder.softValues()：使用软引用存储值。软引用只有在响应内存需要时，才按照全局最近最少使用的顺序回收。考虑到使用软引用的性能影响，我们通常建议使用更有性能预测性的缓存大小限定（见上文，基于容量回收）。使用软引用值的缓存同样用==而不是equals比较值。
```
    @Test
    public void testWeakKey() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .weakValues()
                .weakKeys()
                .softValues()
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.getUnchecked("guava");
        cache.getUnchecked("wangji");

        System.gc();
        TimeUnit.MILLISECONDS.sleep(100);
        Employee employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));
    }
```
###显式清除
任何时候，你都可以显式地清除缓存项，而不是等到它被回收：
个别清除：Cache.invalidate(key) 
批量清除：Cache.invalidateAll(keys) 
清除所有缓存项：Cache.invalidateAll()


###移除监听器
通过CacheBuilder.removalListener(RemovalListener)，你可以声明一个监听器，以便缓存项被移除时做一些额外操作。缓存项被移除时，RemovalListener会获取移除通知[RemovalNotification]，其中包含移除原因[RemovalCause]、键和值。
```
@Test
    public void testCacheRemovedNotification() {
        CacheLoader<String, String> loader = CacheLoader.from(String::toUpperCase);
        RemovalListener<String, String> listener = notification ->
        {
            if (notification.wasEvicted()) {
                RemovalCause cause = notification.getCause();
                log.info("remove cacase is :" + cause.toString());
                log.info("key:" + notification.getKey() + "value:" + notification.getValue());
            }
        };
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .removalListener(listener)// 添加删除监听
                .build(loader);
        cache.getUnchecked("wangji");
        cache.getUnchecked("wangwang");
        cache.getUnchecked("guava");
        cache.getUnchecked("test");
        cache.getUnchecked("test1");
    }
```
警告：默认情况下，监听器方法是在移除缓存时同步调用的。因为缓存的维护和请求响应通常是同时进行的，代价高昂的监听器方法在同步模式下会拖慢正常的缓存请求。在这种情况下，你可以使用RemovalListeners.asynchronous(RemovalListener, Executor)把监听器装饰为异步操作。

###清理什么时候发生？
使用CacheBuilder构建的缓存不会”自动”执行清理和回收工作，也不会在某个缓存项过期后马上清理，也没有诸如此类的清理机制。相反，它会在写操作时顺带做少量的维护工作，或者偶尔在读操作时做——如果写操作实在太少的话。
这样做的原因在于：如果要自动地持续清理缓存，就必须有一个线程，这个线程会和用户操作竞争共享锁。此外，某些环境下线程创建可能受限制，这样CacheBuilder就不可用了。
相反，我们把选择权交到你手里。如果你的缓存是高吞吐的，那就无需担心缓存的维护和清理等工作。如果你的 缓存只会偶尔有写操作，而你又不想清理工作阻碍了读操作，那么可以创建自己的维护线程，以固定的时间间隔调用Cache.cleanUp()。ScheduledExecutorService可以帮助你很好地实现这样的定时调度。


###刷新
刷新和回收不太一样。正如LoadingCache.refresh(K)所声明，刷新表示为键加载新值，这个过程可以是异步的。在刷新操作进行时，缓存仍然可以向其他线程返回旧值，而不像回收操作，读缓存的线程必须等待新值加载完成。
```
 @Test
    public void testCacheRefresh() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CacheLoader<String, Long> cacheLoader = CacheLoader
                .from(k -> {
                    counter.incrementAndGet();
                    log.info("创建 key :" + k);
                    return System.currentTimeMillis();
                });
        LoadingCache<String, Long> cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(2, TimeUnit.SECONDS) // 2s后重新刷新
                .build(cacheLoader);


        Long result1 = cache.getUnchecked("guava");
        TimeUnit.SECONDS.sleep(3);
        Long result2 = cache.getUnchecked("guava");
        log.info(result1.longValue() != result2.longValue() ? "是的" : "否");

    }
```
CacheBuilder.refreshAfterWrite(long, TimeUnit)可以为缓存增加自动定时刷新功能。和expireAfterWrite相反，refreshAfterWrite通过定时刷新可以让缓存项保持可用，但请注意：缓存项只有在被检索时才会真正刷新（如果CacheLoader.refresh实现为异步，那么检索不会被刷新拖慢）。因此，如果你在缓存上同时声明expireAfterWrite和refreshAfterWrite，缓存并不会因为刷新盲目地定时重置，如果缓存项没有被检索，那刷新就不会真的发生，缓存项在过期时间后也变得可以回收。

####操作类
```
public class Employee {
    private final String name;
    private final String dept;
    private final String empID;

    public Employee(String name, String dept, String empID) {
        this.name = name;
        this.dept = dept;
        this.empID = empID;
    }

    public String getName() {
        return name;
    }

    public String getDept() {
        return dept;
    }

    public String getEmpID() {
        return empID;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Name", this.getName()).add("Department", getDept())
                .add("EmployeeID", this.getEmpID()).toString();
    }

}
```
加载类
```
@Slf4j
public class CacheLoaderCreatetor {
    public static com.google.common.cache.CacheLoader<String, Employee> createCacheLoader() {
        return new com.google.common.cache.CacheLoader<String, Employee>() {
            @Override
            public Employee load(String key) throws Exception {
                log.info("加载创建key:" + key);
                return new Employee(key, key + "dept", key + "id");
            }
        };
    }

    public static com.google.common.cache.CacheLoader<String, Employee> createNUllCacheLoader() {
        return new com.google.common.cache.CacheLoader<String, Employee>() {
            @Override
            public Employee load(String key) throws Exception {
                log.info("加载创建key:" + key);
                if (key.equals("null")) {
                    return null;
                }
                return new Employee(key, key + "dept", key + "id");
            }
        };
    }

    public static com.google.common.cache.CacheLoader<String, Optional<Employee>> createNullValueUseOptionalCacheLoader() {
        return new com.google.common.cache.CacheLoader<String, Optional<Employee>>() {
            @Override
            public Optional<Employee> load(String key) throws Exception {
                log.info("加载创建key:" + key);
                if (key.equals("null")) {
                    return Optional.fromNullable(null);
                } else {
                    return Optional.fromNullable( new Employee(key, key + "dept", key + "id"));
                }
            }


        };
    }
}
```
测试类
```
package guava.cache;

import com.google.common.base.Optional;
import com.google.common.cache.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * descrption: guava缓存的使用
 * authohr: wangji
 * date: 2018-02-05 14:05
 */
@Slf4j
public class CacheLoaderTest {
    @Test
    public void testBaisc() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(30L, TimeUnit.MILLISECONDS)
                .build(CacheLoaderCreatetor.createCacheLoader());
        Employee employee = cache.get("wangji");
        log.info("获取结果：" + employee.toString());

        TimeUnit.MILLISECONDS.sleep(31);
        employee = cache.getUnchecked("wangji");
        log.info("重新创建加载信息：" + employee.toString());
    }

    @Test
    public void testSize() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.getUnchecked("wangji");
        cache.getUnchecked("wangwang");
        cache.getUnchecked("old wang");
        assertThat(cache.size(), equalTo(3L));

        cache.getUnchecked("new wang");
        Employee employee = cache.getIfPresent("wangji"); //不会重新加载创建cache
        log.info("最新的把老的替换掉：" + (employee == null ? "是的" : "否"));
        Employee newEmployee = cache.getIfPresent("new wang"); //不会重新加载创建cache
        log.info("获取结果：" + newEmployee);
    }

    @Test
    public void testWeight() throws ExecutionException, InterruptedException {
        //如果不同的高速缓存条目有不同的“权重”，例如，如果你的缓存值有着完全不同的记忆的足迹--你可以用cachebuilder指定一个权重函数。秤（秤）和一个cachebuilder最大缓存量的最大重量（长）
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .maximumWeight(150)
                .weigher(new Weigher<String, Employee>() {
                    public int weigh(String key, Employee employee) {
                        int weight = employee.getName().length() + employee.getEmpID().length() + employee.getDept().length();//权重计算器
                        log.info("weight is :" + weight);
                        return weight;
                    }
                })
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.get("wangji");
        log.info("cacheSize：" + cache.size());
        cache.get("wangwang");
        log.info("cacheSize：" + cache.size());
        cache.get("old wang");
        log.info("cacheSize：" + cache.size());
        cache.get("new wang");
        log.info("cacheSize：" + cache.size());
    }

    /**
     * TTL->time to live
     * Access time => Write/Update/Read
     */
    @Test
    public void testEvictionByAccessTime() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterAccess(2, TimeUnit.SECONDS)
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.getUnchecked("wangji");
        TimeUnit.SECONDS.sleep(3);
        Employee employee = cache.getIfPresent("wangji"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));
        cache.getUnchecked("guava");

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

    }

    /**
     * Write time => write/update
     */
    @Test
    public void testEvictionByWriteTime() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.getUnchecked("guava");
        TimeUnit.SECONDS.sleep(2);
        Employee employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        cache.put("guava", new Employee("guava", "guava" + "dept", "guava" + "id")); //手动插入
        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        cache.put("guava", new Employee("guava", "guava" + "dept", "guava" + "id"));
        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

    }

    /**
     * Strong/soft/weak/Phantom reference
     * https://www.cnblogs.com/daxin/p/5604923.html
     * http://cd826.iteye.com/blog/2036659
     */
    @Test
    public void testWeakKey() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .weakValues()
                .weakKeys()
                .softValues()
                .build(CacheLoaderCreatetor.createCacheLoader());
        cache.getUnchecked("guava");
        cache.getUnchecked("wangji");

        System.gc();
        TimeUnit.MILLISECONDS.sleep(100);
        Employee employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));
    }

    @Test
    public void testSoftKey() throws InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .softValues()
                .build(CacheLoaderCreatetor.createCacheLoader());
        int i = 0;
        for (; ; ) {
            cache.put("Alex" + i, new Employee("Alex" + 1, "Alex" + 1, "Alex" + 1));
            log.info("The Employee [" + (i++) + "] is store into cache.");
            log.info("cache size" + cache.size());
            TimeUnit.MILLISECONDS.sleep(600);
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * 测试为空的情况
     *
     * @throws InterruptedException
     * @throws Exception
     */
    @Test
    public void testLoadNullValue() throws InterruptedException, Exception {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .softValues()
                .build(CacheLoaderCreatetor.createNUllCacheLoader());
        thrown.expect(CacheLoader.InvalidCacheLoadException.class);
        try {
            //不存在创建了一个null的value，不被允许的！
            cache.getUnchecked("null");
        } catch (Exception e) {
            log.error("error", e);
            throw e;
        }
    }

    @Test
    public void testLoadNullValueUseOptional() {
        LoadingCache<String, Optional<Employee>> cache = CacheBuilder.newBuilder().build(CacheLoaderCreatetor.createNullValueUseOptionalCacheLoader());
        Optional<Employee> employeeOptional = cache.getUnchecked("guava");
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            log.info("employee：" + employee.toString());
        }

        Optional<Employee> employeeNull = cache.getUnchecked("null");
        if (!employeeNull.isPresent()) {
            log.info("employee is null");
            Employee def = cache.getUnchecked("null").or(new Employee("default", "default", "default"));
            log.info("employee deful：" + def.toString());
        }
    }

    @Test
    public void testCacheRemovedNotification() {
        CacheLoader<String, String> loader = CacheLoader.from(String::toUpperCase);
        RemovalListener<String, String> listener = notification ->
        {
            if (notification.wasEvicted()) {
                RemovalCause cause = notification.getCause();
                log.info("remove cacase is :" + cause.toString());
                log.info("key:" + notification.getKey() + "value:" + notification.getValue());
            }
        };
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .removalListener(listener)// 添加删除监听
                .build(loader);
        cache.getUnchecked("wangji");
        cache.getUnchecked("wangwang");
        cache.getUnchecked("guava");
        cache.getUnchecked("test");
        cache.getUnchecked("test1");
    }

    @Test
    public void testCachePreLoad() {
        CacheLoader<String, String> loader = CacheLoader.from(String::toUpperCase);
        LoadingCache<String, String> cache = CacheBuilder.newBuilder().build(loader);
        Map<String, String> preData = new HashMap<String, String>() {
            {
                put("guava", "guava");
                put("guava1", "guava1");
            }
        };
        cache.putAll(preData); //提前插入
        log.info("cache size :" + cache.size());
        log.info("guava:" + cache.getUnchecked("guava"));
    }

    @Test
    public void testCacheRefresh() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CacheLoader<String, Long> cacheLoader = CacheLoader
                .from(k -> {
                    counter.incrementAndGet();
                    log.info("创建 key :" + k);
                    return System.currentTimeMillis();
                });
        LoadingCache<String, Long> cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(2, TimeUnit.SECONDS) // 2s后重新刷新
                .build(cacheLoader);


        Long result1 = cache.getUnchecked("guava");
        TimeUnit.SECONDS.sleep(3);
        Long result2 = cache.getUnchecked("guava");
        log.info(result1.longValue() != result2.longValue() ? "是的" : "否");

    }

}

```
