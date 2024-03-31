package com.interview.waiwingchoyProtfolioMonitor.runnable;

import com.interview.waiwingchoyProtfolioMonitor.PortfilioSnapshot;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PortfilioPrinterListener implements Runnable{
    private static PortfilioPrinterListener portfilioPrinterListener = null;
    private static final Object lock = new Object();
    private final BlockingQueue<PortfilioSnapshot> portfilioSnapshots = new LinkedBlockingQueue<>();
    private PortfilioPrinterListener() {
    }

    public static PortfilioPrinterListener getInstance() {
        PortfilioPrinterListener result = portfilioPrinterListener;
        if (result == null) {
            synchronized (lock) {
                result = portfilioPrinterListener;
                if (result == null)
                    portfilioPrinterListener = result = new PortfilioPrinterListener();
            }
        }
        return result;
    }



    public void receivePrintRequest(PortfilioSnapshot portfilioSnapshot) {
        try {
            portfilioSnapshots.put(portfilioSnapshot);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        System.out.println("PortfilioPrinterRunner: start runner ");
        while (true) {
            try {
                PortfilioSnapshot snapshot = portfilioSnapshots.take();
                System.out.println(snapshot.toConsole());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void startPrinter() {
        TaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.execute(this);
    }
}
