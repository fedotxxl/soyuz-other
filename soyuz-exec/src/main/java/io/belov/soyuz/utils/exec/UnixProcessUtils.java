package io.belov.soyuz.utils.exec;

import com.google.common.base.Throwables;

import java.lang.reflect.Field;

/**
 * Created by fbelov on 08.11.15.
 */
public class UnixProcessUtils {

    //http://stackoverflow.com/questions/2950338/how-can-i-kill-a-linux-process-in-java-with-sigkill-process-destroy-does-sigte
    public static int getUnixPID(Process process) {
        try {
            if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
                Class cl = process.getClass();
                Field field = cl.getDeclaredField("pid");
                field.setAccessible(true);
                Object pidObject = field.get(process);
                return (Integer) pidObject;
            } else {
                throw new IllegalArgumentException("Needs to be a UNIXProcess");
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    //http://stackoverflow.com/questions/2950338/how-can-i-kill-a-linux-process-in-java-with-sigkill-process-destroy-does-sigte
    public static int killUnixProcess(Process process) {
        try {
            int pid = getUnixPID(process);
            return Runtime.getRuntime().exec("kill " + pid).waitFor();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
