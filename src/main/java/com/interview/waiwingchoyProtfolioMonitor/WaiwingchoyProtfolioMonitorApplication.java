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
		UniversialCache universe = UniversialCache.getInstance();
		System.out.println("load file ... "+args[0]);
		// read position file and read static content from db, transform them to SecurityStatic
		List<SecurityStatic> securityStaticList = SecuritiesUtils.loadSecurityStaticFromCVSAndDefinitionToList(args[0], universe, securityDefinitionRepository);

		TraderPortfilio traderPortfilio = new TraderPortfilio(securityStaticList);

		// Distinct root symbol from security static and do market data subscription.
		securityStaticList.stream().map(s->universe.getSecurityDefinition(s.symbol()).getRootSymbol()).toList().stream().distinct().forEach(s -> {
			//the universe security definition only has symbol which is in portfolio, somehow root symbol may not be in portfolio, so need to retrieve record.
			SecurityDefinition securityDefinition = securityDefinitionRepository.findBySymbol(s);
			System.out.println("Subscribe ... "+securityDefinition.getSymbol());
			marketDataManager.subscribeMarketData(securityDefinition.getSymbol(), traderPortfilio, securityDefinition);

			System.out.println("set BlackScholesCache ... "+securityDefinition.getSymbol());
			BlackScholesCache blackScholesCache = new BlackScholesCache(securityDefinition);
			universe.setBlackScholesCache(securityDefinition.getSymbol(), blackScholesCache);
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
