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
import java.util.stream.Collectors;

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
	public void run(String[] args) throws Exception {
		if (args.length==0) {
			System.out.println("no portfolio file in the argument");
			System.exit(0);
		}

		MarketDataManager marketDataManager = MarketDataManager.getInstance();
		System.out.println("load file ... "+args[0]);
		// read position file and read static content from db, transform them to SecurityStatic
		List<SecurityStatic> securityStaticList = SecuritiesUtils.loadSecurityStaticFromCVSAndDefinitionToList(args[0], securityDefinitionRepository);
		TraderPortfilio traderPortfilio = new TraderPortfilio(securityStaticList);

		// Distinct root symbol from security static and do market data subscription.
		securityStaticList.stream().map(s->s.securityDefinition().getRootSymbol()).toList().stream().distinct().forEach(s -> {
			SecurityDefinition securityDefinition = securityDefinitionRepository.findBySymbol(s);
			marketDataManager.subscribeMarketData(securityDefinition.getSymbol(), traderPortfilio, securityDefinition);
		});

		// register PortfilioPrinter to portfilio runnable.
		traderPortfilio.registerPortfilioPrinterRunner(PortfilioPrinterListener.getInstance());

		new SimpleAsyncTaskExecutor().execute(traderPortfilio);

		PortfilioPrinterListener.getInstance().startPrinter();
		marketDataManager.startMarketDataProvider();
		while(true) {
			Thread.sleep(100000);
		}
	}
}
