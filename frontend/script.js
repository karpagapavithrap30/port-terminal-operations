const API_BASE_URL = "http://localhost:8080";

async function loadDashboard() {
    try {
        const shippingLines = await fetch(`${API_BASE_URL}/api/shipping-lines`)
            .then(response => response.json());

        const vendors = await fetch(`${API_BASE_URL}/api/vendors`)
            .then(response => response.json());

        const containers = await fetch(`${API_BASE_URL}/api/port/containers`)
            .then(response => response.json());

        const cranes = await fetch(`${API_BASE_URL}/api/port/cranes`)
            .then(response => response.json());

        document.getElementById("shippingLineCount").textContent =
            shippingLines.length;

        document.getElementById("vendorCount").textContent =
            vendors.length;

        document.getElementById("containerCount").textContent =
            containers.length;

        document.getElementById("craneCount").textContent =
            cranes.filter(crane => crane.status === "AVAILABLE").length;

    } catch (error) {
        console.error("Dashboard loading error:", error);
    }
}

loadDashboard();

async function loadReports() {
    try {
        const profitLoss = await fetch(
            `${API_BASE_URL}/api/port/reports/profit-loss`
        ).then(response => response.text());

        const balanceSheet = await fetch(
            `${API_BASE_URL}/api/port/reports/balance-sheet`
        ).then(response => response.text());

        document.getElementById("profitLoss").textContent = profitLoss;
        document.getElementById("balanceSheet").textContent = balanceSheet;

    } catch (error) {
        console.error("Report loading error:", error);
    }
}

loadReports();
async function showShippingLines() {
    try {
        const shippingLines = await fetch(
            `${API_BASE_URL}/api/shipping-lines`
        ).then(response => response.json());

        console.log("Shipping Lines:", shippingLines);

        alert(
            "Shipping Lines:\n\n" +
            shippingLines.map(line => 
                `${line.id}. ${line.name}`
            ).join("\n")
        );

    } catch (error) {
        console.error("Shipping Lines error:", error);
        alert("Unable to load shipping lines.");
    }
}
async function showContainers() {
    try {
        const containers = await fetch(
            `${API_BASE_URL}/api/port/containers`
        ).then(response => response.json());

        console.log("Containers:", containers);

        alert(
            "Containers:\n\n" +
            containers.map(container =>
                `${container.id}. ${container.containerNumber} - ${container.status}`
            ).join("\n")
        );

    } catch (error) {
        console.error("Containers error:", error);
        alert("Unable to load containers.");
    }
}
async function showYardSlots() {
    try {
        const slots = await fetch(
            `${API_BASE_URL}/api/port/slots`
        ).then(response => response.json());

        console.log("Yard Slots:", slots);

        alert(
            "Yard Slots:\n\n" +
            slots.map(slot =>
                `${slot.id}. ${slot.slotCode} - ${slot.zone} - ${slot.status}`
            ).join("\n")
        );

    } catch (error) {
        console.error("Yard Slots error:", error);
        alert("Unable to load yard slots.");
    }
}
async function showCranes() {
    try {
        const cranes = await fetch(
            `${API_BASE_URL}/api/port/cranes`
        ).then(response => response.json());

        console.log("Cranes:", cranes);

        alert(
            "Cranes:\n\n" +
            cranes.map(crane =>
                `${crane.id}. ${crane.craneCode} - ${crane.type} - ${crane.status} - ${crane.currentZone}`
            ).join("\n")
        );

    } catch (error) {
        console.error("Cranes error:", error);
        alert("Unable to load cranes.");
    }
}
async function showPurchaseOrders() {
    try {
        const purchaseOrders = await fetch(
            `${API_BASE_URL}/api/port/purchase-orders`
        ).then(response => response.json());

        console.log("Purchase Orders:", purchaseOrders);

        alert(
            "Purchase Orders:\n\n" +
            purchaseOrders.map(po =>
                `${po.id}. ${po.item} - ₹${po.totalAmount} - ${po.status}`
            ).join("\n")
        );

    } catch (error) {
        console.error("Purchase Orders error:", error);
        alert("Unable to load purchase orders.");
    }
}
async function showSalesOrders() {
    try {
        const salesOrders = await fetch(
            `${API_BASE_URL}/api/port/sales-orders`
        ).then(response => response.json());

        console.log("Sales Orders:", salesOrders);

        alert(
            "Sales Orders:\n\n" +
            salesOrders.map(order =>
                `${order.id}. ${order.service} - ₹${order.totalAmount} - ${order.status}`
            ).join("\n")
        );

    } catch (error) {
        console.error("Sales Orders error:", error);
        alert("Unable to load sales orders.");
    }
}
async function showInvoices() {
    try {
        const invoices = await fetch(
            `${API_BASE_URL}/api/port/customer-invoices`
        ).then(response => response.json());

        console.log("Invoices:", invoices);

        alert(
            "Customer Invoices:\n\n" +
            invoices.map(invoice =>
                `${invoice.id}. Total ₹${invoice.totalAmount} - ${invoice.status}`
            ).join("\n")
        );

    } catch (error) {
        console.error("Invoices error:", error);
        alert("Unable to load invoices.");
    }
}
async function showPayments() {
    try {
        const payments = await fetch(
            `${API_BASE_URL}/api/port/customer-payments`
        ).then(response => response.json());

        console.log("Customer Payments:", payments);

        alert(
            "Customer Payments:\n\n" +
            payments.map(payment =>
                `${payment.id}. ₹${payment.amount} - ${payment.paymentMethod} - ${payment.status}`
            ).join("\n")
        );

    } catch (error) {
        console.error("Payments error:", error);
        alert("Unable to load payments.");
    }
}
async function showReports() {
    try {
        const profitLoss = await fetch(
            `${API_BASE_URL}/api/port/reports/profit-loss`
        ).then(response => response.text());

        const balanceSheet = await fetch(
            `${API_BASE_URL}/api/port/reports/balance-sheet`
        ).then(response => response.text());

        alert(
            "Financial Reports\n\n" +
            "PROFIT & LOSS\n" +
            profitLoss +
            "\n\nBALANCE SHEET\n" +
            balanceSheet
        );

    } catch (error) {
        console.error("Reports error:", error);
        alert("Unable to load reports.");
    }
}
async function showBudget() {
    try {
        const budget = await fetch(
            `${API_BASE_URL}/api/port/budget-variance`
        ).then(response => response.json());

        alert(
            "Budget Variance\n\n" +
            budget.map(item =>
                `${item.id}. ${item.accountName}\n` +
                `Budget: ₹${item.budgetAmount}\n` +
                `Actual: ₹${item.actualAmount}\n` +
                `Variance: ₹${item.varianceAmount}`
            ).join("\n\n")
        );

    } catch (error) {
        console.error("Budget error:", error);
        alert("Unable to load budget.");
    }
}