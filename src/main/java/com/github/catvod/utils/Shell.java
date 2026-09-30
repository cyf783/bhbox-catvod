package com.github.catvod.utils;

public class Shell {

    private static final String TAG = Shell.class.getSimpleName();

    public static int exec(String command) {
        try {
            int code = Runtime.getRuntime().exec(command).waitFor();
            if (code != 0) LOG.d(TAG,String.format("Shell command '%s' failed with exit code '%s'", command, code));
            return code;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}