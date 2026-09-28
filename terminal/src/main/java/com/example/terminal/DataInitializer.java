package com.example.terminal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.terminal.model.Account;
import com.example.terminal.model.AnalyticAccount;
import com.example.terminal.model.Crane;
import com.example.terminal.model.ShippingLine;
import com.example.terminal.model.Vendor;
import com.example.terminal.model.YardSlot;
import com.example.terminal.model.User;
import com.example.terminal.repository.AccountRepository;
import com.example.terminal.repository.AnalyticAccountRepository;
import com.example.terminal.repository.CraneRepository;
import com.example.terminal.repository.ShippingLineRepository;
import com.example.terminal.repository.UserRepository;
import com.example.terminal.repository.VendorRepository;
import com.example.terminal.repository.YardSlotRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ShippingLineRepository shippingLineRepository;
    private final VendorRepository vendorRepository;
    private final YardSlotRepository yardSlotRepository;
    private final CraneRepository craneRepository;
    private final AccountRepository accountRepository;
    private final AnalyticAccountRepository analyticAccountRepository;
    private final UserRepository userRepository;

    public DataInitializer(ShippingLineRepository shippingLineRepository,
                           VendorRepository vendorRepository,
                           YardSlotRepository yardSlotRepository,
                           CraneRepository craneRepository,
                           AccountRepository accountRepository,
                           AnalyticAccountRepository analyticAccountRepository,
                           UserRepository userRepository) {
        this.shippingLineRepository = shippingLineRepository;
        this.vendorRepository = vendorRepository;
        this.yardSlotRepository = yardSlotRepository;
        this.craneRepository = craneRepository;
        this.accountRepository = accountRepository;
        this.analyticAccountRepository = analyticAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed Users
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin", "admin123", "Capt. Alex Mercer", "admin@portops.com", "ADMIN", "Terminal Operations Command"));
            userRepository.save(new User("yardmanager", "yard123", "Marcus Vance", "yard@portops.com", "YARD_MANAGER", "Yard Logistics & Berth Control"));
            userRepository.save(new User("finance", "finance123", "Elena Rostova", "finance@portops.com", "FINANCE_OFFICER", "Finance & Accounting"));
            userRepository.save(new User("shipping", "shipping123", "Sarah Jenkins", "shipping@maersk.com", "SHIPPING_LINE_AGENT", "Shipping Operations"));
        }

        // Seed Chart of Accounts
        if (accountRepository.count() == 0) {
            Account bank = new Account();
            bank.setAccountName("Bank");
            bank.setAccountType("Asset");
            accountRepository.save(bank);

            Account rec = new Account();
            rec.setAccountName("Customer Receivable");
            rec.setAccountType("Asset");
            accountRepository.save(rec);

            Account pay = new Account();
            pay.setAccountName("Vendor Payable");
            pay.setAccountType("Liability");
            accountRepository.save(pay);

            Account hRev = new Account();
            hRev.setAccountName("Terminal Handling Revenue");
            hRev.setAccountType("Revenue");
            accountRepository.save(hRev);

            Account dRev = new Account();
            dRev.setAccountName("Demurrage Revenue");
            dRev.setAccountType("Revenue");
            accountRepository.save(dRev);

            Account fuelExp = new Account();
            fuelExp.setAccountName("Fuel Expense");
            fuelExp.setAccountType("Expense");
            accountRepository.save(fuelExp);

            Account maintExp = new Account();
            maintExp.setAccountName("Maintenance Expense");
            maintExp.setAccountType("Expense");
            accountRepository.save(maintExp);
        }

        // Seed Shipping Lines
        if (shippingLineRepository.count() == 0) {
            ShippingLine maersk = new ShippingLine();
            maersk.setName("Maersk Line");
            maersk.setContactPerson("John Doe");
            maersk.setEmail("dispatch@maersk.com");
            maersk.setPhone("+1-800-MAERSK");
            shippingLineRepository.save(maersk);

            ShippingLine msc = new ShippingLine();
            msc.setName("MSC Mediterranean Shipping");
            msc.setContactPerson("Sarah Smith");
            msc.setEmail("ops@msc.com");
            msc.setPhone("+1-800-MSC-SHIP");
            shippingLineRepository.save(msc);
        }

        // Seed Vendors
        if (vendorRepository.count() == 0) {
            Vendor fuelVendor = new Vendor();
            fuelVendor.setName("Gulf Fuel Supplies Ltd.");
            fuelVendor.setContactPerson("Robert Johnson");
            fuelVendor.setEmail("sales@gulffuel.com");
            fuelVendor.setPhone("+91-9876543210");
            vendorRepository.save(fuelVendor);
        }

        // Seed Yard Slots
        if (yardSlotRepository.count() == 0) {
            for (int i = 1; i <= 5; i++) {
                YardSlot slot = new YardSlot();
                slot.setSlotCode("SLOT-A0" + i);
                slot.setZone("Zone A");
                slot.setStatus("AVAILABLE");
                yardSlotRepository.save(slot);
            }
            for (int i = 1; i <= 5; i++) {
                YardSlot slot = new YardSlot();
                slot.setSlotCode("SLOT-B0" + i);
                slot.setZone("Zone B");
                slot.setStatus("AVAILABLE");
                yardSlotRepository.save(slot);
            }
        }

        // Seed Cranes
        if (craneRepository.count() == 0) {
            Crane crane1 = new Crane();
            crane1.setCraneCode("CRANE-01");
            crane1.setType("Gantry Crane");
            crane1.setStatus("AVAILABLE");
            crane1.setCurrentZone("Zone A");
            craneRepository.save(crane1);

            Crane crane2 = new Crane();
            crane2.setCraneCode("CRANE-02");
            crane2.setType("Reach Stacker");
            crane2.setStatus("AVAILABLE");
            crane2.setCurrentZone("Zone B");
            craneRepository.save(crane2);
        }

        // Seed Analytic Account
        if (analyticAccountRepository.count() == 0) {
            AnalyticAccount analytic = new AnalyticAccount();
            analytic.setAccountName("Terminal 2 Deepwater Yard");
            analytic.setBudgetAmount(50000.0);
            analytic.setActualAmount(0.0);
            analyticAccountRepository.save(analytic);
        }
    }
}
