package gg.galaxygaming.ts.Wrapper;

import com.sun.jna.*;
import com.sun.jna.ptr.*;
import gg.galaxygaming.ts.JanetTS;

public class Wrapper {

    // This is the standard, stable way of mapping, which supports extensive
    // customization and mapping of Java to native types.

    /*public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary) Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);

        void printf(String format, Object... args);
    }*/
    private static LongByReference CONNECTION_ID;

    public interface TSLibrary extends Library {
        //TSLibrary INSTANCE = (TSLibrary)Native.loadLibrary((Platform.isWindows() ? Platform.is64Bit() ? "lib/ts3client_win64.dll" : "lib/libts3client_linux_x86.so" :
                //Platform.is64Bit() ? "lib/libts3client_linux_amd64.so" : "lib/ts3client_win32.dll"), TSLibrary.class);
        TSLibrary INSTANCE = (TSLibrary)Native.loadLibrary((Platform.isWindows() ? (Platform.is64Bit() ? "bin/ts3server_win64.dll" : "bin/ts3server_win32.dll") :
                (Platform.is64Bit() ? "bin/libts3server_linux_amd64.so" : "bin/libts3server_linux_x86.so")), TSLibrary.class);

        int ts3server_initServerLib(PointerByReference functionPointers, int usedLogTypes, String logFileFolder);

        int ts3server_destroyServerLib();

        int ts3server_freeMemory(Pointer pointer);
    }

    public static void main(String[] args) {
        try {
            PointerByReference p = new PointerByReference();
            int error = TSLibrary.INSTANCE.ts3server_initServerLib(p, 0x0002, null);
            System.out.println(error);
            /*CONNECTION_ID = new LongByReference();
            error = TSLibrary.INSTANCE.ts3client_spawnNewServerConnectionHandler(0, CONNECTION_ID);
            System.out.println(error);
            System.out.println(CONNECTION_ID.getValue());


            PointerByReference name = new PointerByReference();
            error = TSLibrary.INSTANCE.ts3client_createIdentity(name);
            System.out.println(error);
            error = TSLibrary.INSTANCE.ts3client_startConnection(CONNECTION_ID, name.getValue().getString(0), JanetTS.getInstance().getConfig().getString("tsHost"), 9987, "JanetC", null, "", "");
            System.out.println(error);
            PointerByReference rcode = new PointerByReference();
            TSLibrary.INSTANCE.ts3client_requestSendChannelTextMsg(CONNECTION_ID, "test", 0, rcode);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exit() {
        try {
            TSLibrary.INSTANCE.ts3server_destroyServerLib();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}