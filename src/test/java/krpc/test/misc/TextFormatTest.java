package krpc.test.misc;

import com.google.protobuf.Message;
import com.xxx.userservice.proto.*;
import krpc.rpc.monitor.JsonLogFormatter;
import krpc.rpc.monitor.SimpleLogFormatter;
import org.junit.Test;

public class TextFormatTest {

    @Test
    public void test1() throws Exception {

        SimpleLogFormatter f = new SimpleLogFormatter();
        f.config("maxRepeatedSizeToLog=2");
        JsonLogFormatter f2 = new JsonLogFormatter();
        f2.config("maxRepeatedSizeToLog=2");

        Message m1 = LoginReq.newBuilder().setUserName("mike").setPassword("how ^ are you :").build();
        Message m2 = LoginRes.newBuilder().setRetCode(0).setRetMsg("hello, friend mike. receive req#").build();
        System.out.println("s=" + f.toLogStr(m1));
        System.out.println("s=" + f2.toLogStr(m1));

        System.out.println("s=" + f.toLogStr(m2));
        System.out.println("s=" + f2.toLogStr(m2));

        OrderItemAttr attr1 = OrderItemAttr.newBuilder().setName("color").setValue("red").build();
        OrderItemAttr attr2 = OrderItemAttr.newBuilder().setName("weight").setValue("1.3kg").build();
        OrderItem item1 = OrderItem.newBuilder().setItemId("111").setName("apple").addAttrs(attr1).addAttrs(attr2).build();
        OrderItem item2 = OrderItem.newBuilder().setItemId("222").setPrice(132).addAttrs(attr1).addAttrs(attr2).build();
        Order o = Order.newBuilder().addItems(item1).addItems(item2).setOrderId("100001").build();
        System.out.println("s=" + f.toLogStr(o));
        System.out.println("s=" + f2.toLogStr(o));

    }

}

