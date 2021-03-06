package krpc.test.registry;

import com.xxx.userservice.proto.*;
import krpc.rpc.bootstrap.Bootstrap;
import krpc.rpc.bootstrap.RegistryConfig;
import krpc.rpc.bootstrap.RpcApp;
import krpc.rpc.bootstrap.ServiceConfig;
import krpc.rpc.core.RpcContextData;
import krpc.rpc.core.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServer1 {

    static Logger log = LoggerFactory.getLogger(RpcServer1.class);

    public static void main(String[] args) throws Exception {

        UserServiceImpl impl = new UserServiceImpl(); // user code is here

        RpcApp app = new Bootstrap()
                .addRegistry(new RegistryConfig().setType("consul").setAddrs("10.1.10.234:8123")//.setAclToken("123456"))
                                .setAclToken("4877e765-9bcd-ca0e-74b6-a0cbe9f44f7b")
                        //.setAclToken("32f3de5f-c325-22a5-1561-46594a0b7271")
                        //.setAclToken("46382326-1163-f7a2-40e8-c241cfaca4f6")
                        //.setAclToken("8d3e5594-2c17-d025-731a-111111111")
                        )
                //.addService(new ServiceConfig().setInterfaceName(UserService.class.getName()).setImpl(impl).setRegistryNames("consul"))
                //.addRegistry(new RegistryConfig().setType("etcd").setAddrs("192.168.31.144:2379"))
                //.addService(new ServiceConfig().setInterfaceName(UserService.class.getName()).setImpl(impl).setRegistryNames("etcd"))
                //.addRegistry(new RegistryConfig().setType("zookeeper").setAddrs("192.168.31.144:2181"))
                //.addService(new ServiceConfig().setInterfaceName(UserService.class.getName()).setImpl(impl).setRegistryNames("zookeeper"))
                //.addRegistry(new RegistryConfig().setType("jedis").setAddrs("127.0.0.1:6379"))
                //.addService(new ServiceConfig().setInterfaceName(UserService.class.getName()).setImpl(impl).setRegistryNames("jedis"))
                .addService(new ServiceConfig().setInterfaceName(UserService.class.getName()).setImpl(impl))
                .build();

        app.initAndStart();

        Thread.sleep(50000000);

        app.stopAndClose();

    }
}

class UserServiceImpl implements UserService {

    static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public LoginRes login(LoginReq req) {

        RpcContextData ctx = ServerContext.get();
        log.info("login received, peers=" + ctx.getMeta().getTrace().getPeers());
        return LoginRes.newBuilder().setRetCode(0).setRetMsg("hello, friend. receive req ").build();
    }
    public Login2Res login2(Login2Req req) {
        return Login2Res.ok();
    }
    public UpdateProfileRes updateProfile(UpdateProfileReq req) {
        UpdateProfileRes res = UpdateProfileRes.newBuilder().setRetCode(-100002).build();
        return res;
    }


}