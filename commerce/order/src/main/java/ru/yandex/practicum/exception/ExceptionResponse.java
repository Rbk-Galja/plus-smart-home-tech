package ru.yandex.practicum.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

public class ExceptionResponse {
    private Cause cause;

    public ExceptionResponse(Cause cause) {
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Cause {
        List<StackTraceElementDto> stackTrace;
        String message;
        String localizedMessage;

        public Cause(List<StackTraceElementDto> stackTrace, String message, String localizedMessage) {
            this.stackTrace = stackTrace;
            this.message = message;
            this.localizedMessage = localizedMessage;
        }
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class StackTraceElementDto {
        String classLoaderName;
        String moduleName;
        String moduleVersion;
        String methodName;
        String fileName;
        int lineNumber;
        String className;
        boolean nativeMethod;

        public StackTraceElementDto(StackTraceElement element) {
            this.classLoaderName = null;
            this.moduleName = element.getModuleName();
            this.moduleVersion = element.getModuleVersion();
            this.methodName = element.getMethodName();
            this.fileName = element.getFileName();
            this.lineNumber = element.getLineNumber();
            this.className = element.getClassName();
            this.nativeMethod = element.isNativeMethod();
        }
    }
}
