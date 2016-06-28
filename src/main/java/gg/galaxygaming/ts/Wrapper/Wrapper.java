package gg.galaxygaming.ts.Wrapper;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import gg.galaxygaming.ts.JanetTS;
import gg.galaxygaming.ts.Utils;

public class Wrapper {

    // This is the standard, stable way of mapping, which supports extensive
    // customization and mapping of Java to native types.

    /*public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary) Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);

        void printf(String format, Object... args);
    }*/
    private static LongByReference CONNECTION_ID;
    private static String IDENTITY;

    public interface TSLibrary extends Library {
        TSLibrary INSTANCE = (TSLibrary) Native.loadLibrary((Platform.isWindows() ? Platform.is64Bit() ? "lib/ts3client_win64.dll" : "lib/libts3client_linux_x86.so" :
                Platform.is64Bit() ? "lib/libts3client_linux_amd64.so" : "lib/ts3client_win32.dll"), TSLibrary.class);
        //TSLibrary INSTANCE = (TSLibrary)Native.loadLibrary((Platform.isWindows() ? (Platform.is64Bit() ? "bin/ts3server_win64.dll" : "bin/ts3server_win32.dll") :
                //(Platform.is64Bit() ? "bin/libts3server_linux_amd64.so" : "bin/libts3server_linux_x86.so")), TSLibrary.class);

        int ts3client_initClientLib(PointerByReference functionPointers, PointerByReference functionRarePointers, int usedLogTypes, String logFileFolder, String resourcesFolder);
        int ts3client_spawnNewServerConnectionHandler(int port, LongByReference result);
        int ts3client_createIdentity(String[] result);
        int ts3client_identityStringToUniqueIdentifier(String identityString, String[] result);
        int ts3client_startConnection(long serverConnectionHandlerID, String identity, String ip, int port, String nickname, String[] defaultChannelArray, String defaultChannelPassword, String serverPassword);
        int ts3client_getClientLibVersion(String[] result);
        int ts3client_stopConnection(long serverConnectionHandlerID, String quitMessage);
        int ts3client_destroyServerConnectionHandler(long serverConnectionHandlerID);
        int ts3client_destroyClientLib();
        int ts3client_freeMemory(Pointer pointer);
        //int ts3client_requestSendChannelTextMsg(PointerByReference serverConnectionHandlerID, String message, String returnCode);
    }

    public static void main(String[] args) {
        try {
            PointerByReference p = new PointerByReference();
            int error = TSLibrary.INSTANCE.ts3client_initClientLib(p, null, 0x0002, null, "lib/");
            System.out.println(error);
            CONNECTION_ID = new LongByReference();
            error = TSLibrary.INSTANCE.ts3client_spawnNewServerConnectionHandler(0, CONNECTION_ID);
            System.out.println(error);
            System.out.println(CONNECTION_ID.getValue());

            //final PointerByReference name = new PointerByReference();
            String[] name = new String[1];
            error = TSLibrary.INSTANCE.ts3client_createIdentity(name);
            System.out.println(error);
            //final Pointer pnt = name.getValue();
            IDENTITY = name[0];
            //IDENTITY = pnt.getString(0);
            //TSLibrary.INSTANCE.ts3client_freeMemory(pnt);
            System.out.println(IDENTITY);
            //String[] id = new String[1];
            //TSLibrary.INSTANCE.ts3client_identityStringToUniqueIdentifier(name[0], id);
            //System.out.println(id[0]);
            String ip = JanetTS.getInstance().getConfig().getString("tsHost");
            int port = 9987;
            if (ip.contains(":")) {
                String[] pieces = ip.split(":");
                ip = pieces[0];
                if (Utils.isLegal(pieces[1]))
                    port = Integer.parseInt(pieces[1]);
            }
            //error = TSLibrary.INSTANCE.ts3client_startConnection(CONNECTION_ID.getValue(), IDENTITY, ip, port, "Janet", null, "", "");
            System.out.println(error);
            //PointerByReference rcode = new PointerByReference();
            //TSLibrary.INSTANCE.ts3client_requestSendChannelTextMsg(CONNECTION_ID, "test", 0, rcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exit() {
        try {
            TSLibrary.INSTANCE.ts3client_stopConnection(CONNECTION_ID.getValue(), "Disconnected.");
            TSLibrary.INSTANCE.ts3client_destroyServerConnectionHandler(CONNECTION_ID.getValue());
            TSLibrary.INSTANCE.ts3client_destroyClientLib();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}