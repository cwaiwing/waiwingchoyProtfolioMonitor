package com.interview.waiwingchoyProtfolioMonitor;

import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;
import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityStatic;
import com.interview.waiwingchoyProtfolioMonitor.repository.SecurityDefinitionRepository;
import com.interview.waiwingchoyProtfolioMonitor.runnable.PortfilioPrinterListener;
import com.interview.waiwingchoyProtfolioMonitor.util.SecuritiesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SpringBootApplication
@Component
public class WaiwingchoyProtfolioMonitorApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WaiwingchoyProtfolioMonitorApplication.class, args);
	}

	private final SecurityDefinitionRepository securityDefinitionRepository;
	@Autowired
	public WaiwingchoyProtfolioMonitorApplication(SecurityDefinitionRepository securityDefinitionRepository) {
		this.securityDefinitionRepository=securityDefinitionRepository;
	}
	@Override
	public void run(String... args) throws Exception {

		MarketDataManager marketDataManager = MarketDataManager.getInstance();
		System.out.println("load file ... "+args[0]);
		// read position file
//		ConcurrentHashMap<String,SecurityStatic> securityStaticMap = SecuritiesUtils.loadSecurityStaticFromCVSAndDefinition(args[0], securityDefinitionRepository);
		List<SecurityStatic> securityStaticList = SecuritiesUtils.loadSecurityStaticFromCVSAndDefinitionToList(args[0], securityDefinitionRepository);
		TraderPortfilio traderPortfilio = new TraderPortfilio(securityStaticList);

		List<String> subSymbol = new CopyOnWriteArrayList<>();
		for (SecurityStatic securityStatic : securityStaticList) {
			String rootSymbol = securityStatic.securityDefinition().getRootSymbol();
			if (!subSymbol.contains(rootSymbol)) {
				subSymbol.add(rootSymbol);
				SecurityDefinition securityDefinition = securityDefinitionRepository.findBySymbol(rootSymbol);
				marketDataManager.subscribeMarketData(rootSymbol, traderPortfilio, securityDefinition);
			}
		}

		traderPortfilio.registerPortfilioPrinterRunner(PortfilioPrinterListener.getInstance());

		List<Tick> ticks = new CopyOnWriteArrayList<>();
		Tick tick1 = new Tick("AAPL", 110.0, 0);
		Tick tick2 = new Tick("TELSA", 450.0, 0);
		ticks.add(tick1);
		ticks.add(tick2);
		traderPortfilio.setInitTicks(ticks);

		new SimpleAsyncTaskExecutor().execute(traderPortfilio);

		PortfilioPrinterListener.getInstance().startPrinter();
		marketDataManager.startMarketDataProvider();
		while(true) {
			Thread.sleep(100000);
		}
	}
}
