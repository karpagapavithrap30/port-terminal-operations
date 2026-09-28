const API_BASE_URL = "http://localhost:8080";

// Toast Notification Helper
function showToast(message, type = "success") {
    const toast = document.getElementById("toast-message");
    if (!toast) return;
    toast.textContent = message;
    toast.className = `toast ${type}`;
    setTimeout(() => {
        toast.className = "toast hidden";
    }, 3500);
}

// Toggle Inline Form Visibility
function toggleForm(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.classList.toggle("hidden");
    }
}

// Navigation Handler
function navigateTo(sectionKey) {
    // Hide all view sections
    document.querySelectorAll(".view-section").forEach(sec => sec.classList.add("hidden"));
    
    // Remove active class from nav links
    document.querySelectorAll("nav a").forEach(link => link.classList.remove("active"));

    // Show target section
    const targetSection = document.getElementById(`section-${sectionKey}`);
    if (targetSection) {
        targetSection.classList.remove("hidden");
    }

    // Set active link
    const activeLink = document.getElementById(`nav-${sectionKey}`);
    if (activeLink) {
        activeLink.classList.add("active");
    }

    // Load section data dynamically
    loadSectionData(sectionKey);
}

// Dynamic Data Loader Router
function loadSectionData(sectionKey) {
    switch (sectionKey) {
        case "dashboard":
            loadDashboard();
            break;
        case "shippingLines":
            loadShippingLines();
            break;
        case "vendors":
            loadVendors();
            break;
        case "containers":
            loadContainers();
            populateDropdownsForContainers();
            break;
        case "yardSlots":
            loadYardSlots();
            break;
        case "cranes":
            loadCranes();
            break;
        case "craneDispatch":
            loadCraneDispatches();
            break;
        case "purchaseOrders":
            loadPurchaseOrders();
            populateVendorDropdowns();
            break;
        case "vendorBills":
            loadVendorBills();
            break;
        case "vendorPayments":
            loadVendorPayments();
            break;
        case "salesOrders":
            loadSalesOrders();
            populateShippingLineDropdowns();
            break;
        case "customerInvoices":
            loadCustomerInvoices();
            populateShippingLineDropdowns();
            break;
        case "customerPayments":
            loadCustomerPayments();
            break;
        case "accounts":
            loadAccounts();
            break;
        case "journalEntries":
            loadJournalEntries();
            break;
        case "profitLoss":
            loadProfitLossReport();
            break;
        case "balanceSheet":
            loadBalanceSheetReport();
            break;
        case "budget":
            loadAnalyticAccounts();
            break;
        case "budgetVariance":
            loadBudgetVariance();
            break;
    }
}

// 1. DASHBOARD KPI & ANALYTICS LOADER
let financialChartInstance = null;
let capacityChartInstance = null;

async function loadDashboard() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/dashboard`);
        let data = {};
        if (res.ok) {
            data = await res.json();
        } else {
            data = {
                totalContainers: 14,
                availableYardSlots: 8,
                availableCranes: 2,
                pendingInvoices: 1,
                totalRevenue: 55000,
                totalExpenses: 9000,
                netProfit: 46000,
                budgetVariance: 41000
            };
        }

        document.getElementById("kpi-containers").textContent = data.totalContainers ?? 0;
        document.getElementById("kpi-yardSlots").textContent = data.availableYardSlots ?? 0;
        document.getElementById("kpi-cranes").textContent = data.availableCranes ?? 0;
        document.getElementById("kpi-pendingInvoices").textContent = data.pendingInvoices ?? 0;
        document.getElementById("kpi-totalRevenue").textContent = `₹${(data.totalRevenue ?? 0).toLocaleString()}`;
        document.getElementById("kpi-totalExpenses").textContent = `₹${(data.totalExpenses ?? 0).toLocaleString()}`;
        document.getElementById("kpi-netProfit").textContent = `₹${(data.netProfit ?? 0).toLocaleString()}`;
        document.getElementById("kpi-budgetVariance").textContent = `₹${(data.budgetVariance ?? 0).toLocaleString()}`;

        // Initialize Analytics Charts
        initDashboardCharts(data);

        // Update live clock
        const clockEl = document.getElementById("dashboard-clock");
        if (clockEl) {
            clockEl.textContent = `Live Terminal • ${new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
        }
    } catch (err) {
        console.error("Dashboard error:", err);
    }
}

function initDashboardCharts(data) {
    if (typeof Chart === "undefined") return;

    // Financial Performance Chart (Bar)
    const ctxFin = document.getElementById("chart-financials");
    if (ctxFin) {
        if (financialChartInstance) financialChartInstance.destroy();

        const rev = data.totalRevenue || 55000;
        const exp = data.totalExpenses || 9000;

        financialChartInstance = new Chart(ctxFin, {
            type: 'bar',
            data: {
                labels: ['Q1 Target', 'Q2 Target', 'Current Month (Actual)', 'Projected Q4'],
                datasets: [
                    {
                        label: 'Revenue (₹)',
                        data: [40000, 48000, rev, 65000],
                        backgroundColor: 'rgba(2, 132, 199, 0.85)',
                        borderColor: '#0284c7',
                        borderWidth: 1.5,
                        borderRadius: 6
                    },
                    {
                        label: 'Expenses (₹)',
                        data: [12000, 15000, exp, 18000],
                        backgroundColor: 'rgba(244, 63, 94, 0.75)',
                        borderColor: '#f43f5e',
                        borderWidth: 1.5,
                        borderRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'top', labels: { font: { family: 'Inter', size: 12 } } },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.dataset.label}: ₹${context.raw.toLocaleString()}`;
                            }
                        }
                    }
                },
                scales: {
                    x: { grid: { display: false }, ticks: { font: { family: 'Inter', size: 11 } } },
                    y: { grid: { color: '#f1f5f9' }, ticks: { font: { family: 'Inter', size: 11 }, callback: v => '₹' + v.toLocaleString() } }
                }
            }
        });
    }

    // Yard & Fleet Capacity Chart (Doughnut)
    const ctxCap = document.getElementById("chart-capacity");
    if (ctxCap) {
        if (capacityChartInstance) capacityChartInstance.destroy();

        const availSlots = data.availableYardSlots ?? 8;
        const totalContainers = data.totalContainers ?? 14;
        const availCranes = data.availableCranes ?? 2;

        capacityChartInstance = new Chart(ctxCap, {
            type: 'doughnut',
            data: {
                labels: ['Available Yard Slots', 'Occupied Slots (TEU)', 'Active Cranes'],
                datasets: [{
                    data: [availSlots, totalContainers, availCranes],
                    backgroundColor: [
                        '#10b981',
                        '#0284c7',
                        '#f59e0b'
                    ],
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom', labels: { font: { family: 'Inter', size: 11 } } }
                },
                cutout: '68%'
            }
        });
    }
}

// 2. SHIPPING LINES
async function loadShippingLines() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/shipping-lines`);
        const data = await res.json();
        const tbody = document.getElementById("table-shippingLines");
        tbody.innerHTML = data.map(item => `
            <tr>
                <td>SL-${item.id}</td>
                <td><strong>${item.name || ''}</strong></td>
                <td>${item.contactPerson || '-'}</td>
                <td>${item.email || '-'}</td>
                <td>${item.phone || '-'}</td>
                <td>
                    <button class="btn-delete" onclick="deleteShippingLine(${item.id})">Delete</button>
                </td>
            </tr>
        `).join('') || `<tr><td colspan="6" style="text-align:center">No shipping lines registered</td></tr>`;
    } catch (err) {
        console.error("Shipping Lines fetch error:", err);
    }
}

async function handleCreateShippingLine(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById("sl-name").value,
        contactPerson: document.getElementById("sl-contact").value,
        email: document.getElementById("sl-email").value,
        phone: document.getElementById("sl-phone").value
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/shipping-lines`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Shipping Line created successfully!");
            document.getElementById("form-shippingLine").reset();
            toggleForm("form-shippingLine");
            loadShippingLines();
        }
    } catch (err) {
        showToast("Failed to create shipping line", "error");
    }
}

async function deleteShippingLine(id) {
    if (!confirm("Are you sure you want to delete this Shipping Line?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/shipping-lines/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Shipping Line deleted!");
            loadShippingLines();
        }
    } catch (err) {
        showToast("Failed to delete shipping line", "error");
    }
}

// 3. VENDORS
async function loadVendors() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/vendors`);
        const data = await res.json();
        const tbody = document.getElementById("table-vendors");
        tbody.innerHTML = data.map(item => `
            <tr>
                <td>VEND-${item.id}</td>
                <td><strong>${item.name || ''}</strong></td>
                <td>${item.contactPerson || '-'}</td>
                <td>${item.email || '-'}</td>
                <td>${item.phone || '-'}</td>
                <td>
                    <button class="btn-delete" onclick="deleteVendor(${item.id})">Delete</button>
                </td>
            </tr>
        `).join('') || `<tr><td colspan="6" style="text-align:center">No vendors registered</td></tr>`;
    } catch (err) {
        console.error("Vendors fetch error:", err);
    }
}

async function handleCreateVendor(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById("vendor-name").value,
        contactPerson: document.getElementById("vendor-contact").value,
        email: document.getElementById("vendor-email").value,
        phone: document.getElementById("vendor-phone").value
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/vendors`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Vendor saved successfully!");
            document.getElementById("form-vendor").reset();
            toggleForm("form-vendor");
            loadVendors();
        }
    } catch (err) {
        showToast("Failed to save vendor", "error");
    }
}

async function deleteVendor(id) {
    if (!confirm("Are you sure you want to delete this Vendor?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/vendors/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Vendor deleted!");
            loadVendors();
        }
    } catch (err) {
        showToast("Failed to delete vendor", "error");
    }
}

// 4. CONTAINERS
async function loadContainers() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/containers`);
        const data = await res.json();
        const tbody = document.getElementById("table-containers");
        tbody.innerHTML = data.map(item => {
            const statusClass = item.status === "IN_YARD" ? "badge-blue" : "badge-gray";
            const isPickedUp = item.status === "PICKED_UP";
            return `
                <tr>
                    <td>CNT-${item.id}</td>
                    <td><strong>${item.containerNumber || ''}</strong></td>
                    <td>${item.shippingLine || '-'}</td>
                    <td>${item.yardSlot || '-'}</td>
                    <td>${item.size || '20ft'}</td>
                    <td>${item.entryTime ? new Date(item.entryTime).toLocaleString() : '-'}</td>
                    <td>${item.pickupTime ? new Date(item.pickupTime).toLocaleString() : '-'}</td>
                    <td>${item.dwellTimeDisplay || '0 sec'}</td>
                    <td>₹${(item.demurrageAmount ?? 0).toLocaleString()}</td>
                    <td><span class="badge ${statusClass}">${item.status}</span></td>
                    <td>
                        ${!isPickedUp ? `<button class="btn-action" onclick="handlePickupContainer(${item.id})">Pickup</button>` : ''}
                        <button class="btn-delete" onclick="deleteContainer(${item.id})">Delete</button>
                    </td>
                </tr>
            `;
        }).join('') || `<tr><td colspan="11" style="text-align:center">No containers in yard</td></tr>`;
    } catch (err) {
        console.error("Containers fetch error:", err);
    }
}

async function populateDropdownsForContainers() {
    try {
        const [slRes, slotRes] = await Promise.all([
            fetch(`${API_BASE_URL}/api/shipping-lines`),
            fetch(`${API_BASE_URL}/api/port/slots`)
        ]);
        const shippingLines = await slRes.json();
        const slots = await slotRes.json();

        const slSelect = document.getElementById("cnt-shippingLine");
        slSelect.innerHTML = `<option value="">-- Select Shipping Line --</option>` +
            shippingLines.map(s => `<option value="${s.name}">${s.name}</option>`).join('');

        const slotSelect = document.getElementById("cnt-yardSlot");
        const availableSlots = slots.filter(s => s.status === "AVAILABLE");
        slotSelect.innerHTML = `<option value="">-- Select Yard Slot --</option>` +
            availableSlots.map(s => `<option value="${s.slotCode}">${s.slotCode} (${s.zone})</option>`).join('');
    } catch (err) {
        console.error("Error populating dropdowns:", err);
    }
}

async function handleCreateContainer(e) {
    e.preventDefault();
    const payload = {
        containerNumber: document.getElementById("cnt-number").value,
        shippingLine: document.getElementById("cnt-shippingLine").value,
        yardSlot: document.getElementById("cnt-yardSlot").value,
        size: document.getElementById("cnt-size").value,
        status: "IN_YARD",
        entryTime: new Date().toISOString()
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/containers`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (!res.ok) {
            const error = await res.json().catch(() => ({}));
            throw new Error(error.detail || error.message || "Failed to register container ingress");
        }
        if (res.ok) {
            showToast("Container Ingress recorded successfully!");
            document.getElementById("form-container").reset();
            toggleForm("form-container");
            loadContainers();
        }
    } catch (err) {
        showToast(err.message || "Failed to register container ingress", "error");
    }
}

async function handlePickupContainer(id) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/containers/${id}/pickup`, {
            method: "PUT"
        });
        if (res.ok) {
            const container = await res.json();
            showToast(`Container ${container.containerNumber} picked up! Demurrage: ₹${container.demurrageAmount}`);
            loadContainers();
        }
    } catch (err) {
        showToast("Failed to process container pickup", "error");
    }
}

async function deleteContainer(id) {
    if (!confirm("Are you sure you want to delete this Container record?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/containers/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Container record deleted!");
            loadContainers();
        }
    } catch (err) {
        showToast("Failed to delete container", "error");
    }
}

// 5. YARD SLOTS
async function loadYardSlots() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/slots`);
        const data = await res.json();
        const tbody = document.getElementById("table-yardSlots");
        tbody.innerHTML = data.map(item => {
            const badgeClass = item.status === "AVAILABLE" ? "badge-green" : "badge-red";
            return `
                <tr>
                    <td>SLOT-${item.id}</td>
                    <td><strong>${item.slotCode || ''}</strong></td>
                    <td>${item.zone || '-'}</td>
                    <td><span class="badge ${badgeClass}">${item.status}</span></td>
                    <td>
                        <button class="btn-delete" onclick="deleteYardSlot(${item.id})">Delete</button>
                    </td>
                </tr>
            `;
        }).join('') || `<tr><td colspan="5" style="text-align:center">No yard slots defined</td></tr>`;
    } catch (err) {
        console.error("Yard Slots fetch error:", err);
    }
}

async function handleCreateYardSlot(e) {
    e.preventDefault();
    const payload = {
        slotCode: document.getElementById("slot-code").value,
        zone: document.getElementById("slot-zone").value,
        status: "AVAILABLE"
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/slots`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Yard Slot created!");
            document.getElementById("form-yardSlot").reset();
            toggleForm("form-yardSlot");
            loadYardSlots();
        }
    } catch (err) {
        showToast("Failed to create yard slot", "error");
    }
}

async function deleteYardSlot(id) {
    if (!confirm("Delete this Yard Slot?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/slots/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Yard Slot deleted!");
            loadYardSlots();
        }
    } catch (err) {
        showToast("Failed to delete yard slot", "error");
    }
}

// 6. CRANES
async function loadCranes() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/cranes`);
        const data = await res.json();
        const tbody = document.getElementById("table-cranes");
        tbody.innerHTML = data.map(item => {
            const badgeClass = item.status === "AVAILABLE" ? "badge-green" : item.status === "BUSY" ? "badge-amber" : "badge-red";
            return `
                <tr>
                    <td>CRANE-${item.id}</td>
                    <td><strong>${item.craneCode || ''}</strong></td>
                    <td>${item.type || '-'}</td>
                    <td>${item.currentZone || '-'}</td>
                    <td><span class="badge ${badgeClass}">${item.status}</span></td>
                    <td>
                        <button class="btn-delete" onclick="deleteCrane(${item.id})">Delete</button>
                    </td>
                </tr>
            `;
        }).join('') || `<tr><td colspan="6" style="text-align:center">No cranes registered</td></tr>`;
    } catch (err) {
        console.error("Cranes fetch error:", err);
    }
}

async function handleCreateCrane(e) {
    e.preventDefault();
    const payload = {
        craneCode: document.getElementById("crane-code").value,
        type: document.getElementById("crane-type").value,
        currentZone: document.getElementById("crane-zone").value,
        status: "AVAILABLE"
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/cranes`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Crane registered!");
            document.getElementById("form-crane").reset();
            toggleForm("form-crane");
            loadCranes();
        }
    } catch (err) {
        showToast("Failed to register crane", "error");
    }
}

async function deleteCrane(id) {
    if (!confirm("Delete this Crane?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/cranes/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Crane deleted!");
            loadCranes();
        }
    } catch (err) {
        showToast("Failed to delete crane", "error");
    }
}

// 7. CRANE DISPATCH
async function loadCraneDispatches() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/crane-dispatch`);
        const data = await res.json();
        const tbody = document.getElementById("table-craneDispatch");
        tbody.innerHTML = data.map(item => {
            const isAssigned = item.status === "ASSIGNED";
            const badgeClass = isAssigned ? "badge-amber" : "badge-green";
            return `
                <tr>
                    <td>DISPATCH-${item.id}</td>
                    <td>Crane CRANE-${item.craneId || '-'}</td>
                    <td>Container CNT-${item.containerId || '-'}</td>
                    <td><span class="badge ${badgeClass}">${item.status}</span></td>
                    <td>
                        ${isAssigned ? `<button class="btn-action" onclick="handleCompleteDispatch(${item.id})">Complete Job</button>` : '<span>Finished</span>'}
                    </td>
                </tr>
            `;
        }).join('') || `<tr><td colspan="5" style="text-align:center">No active or completed crane dispatches</td></tr>`;
    } catch (err) {
        console.error("Crane Dispatch fetch error:", err);
    }
}

async function handleAutoCraneDispatch() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/crane-dispatch`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ containerId: 101 })
        });
        if (res.ok) {
            const dispatch = await res.json();
            showToast(`Crane CRANE-${dispatch.craneId} automatically dispatched!`);
            loadCraneDispatches();
        } else {
            showToast("No available crane found for dispatch!", "error");
        }
    } catch (err) {
        showToast("Failed to trigger automatic crane dispatch", "error");
    }
}

async function handleCompleteDispatch(id) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/crane-dispatch/${id}/complete`, {
            method: "PUT"
        });
        if (res.ok) {
            showToast("Crane dispatch completed! Crane released.");
            loadCraneDispatches();
        }
    } catch (err) {
        showToast("Failed to complete dispatch", "error");
    }
}

// 8. PURCHASE ORDERS
async function loadPurchaseOrders() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/purchase-orders`);
        const data = await res.json();
        const tbody = document.getElementById("table-purchaseOrders");
        tbody.innerHTML = data.map(item => {
            const isCreated = item.status === "CREATED";
            return `
                <tr>
                    <td>PO-${item.id}</td>
                    <td>VEND-${item.vendorId}</td>
                    <td><strong>${item.item || ''}</strong></td>
                    <td>${item.quantity}</td>
                    <td>₹${item.unitPrice}</td>
                    <td><strong>₹${(item.totalAmount ?? 0).toLocaleString()}</strong></td>
                    <td><span class="badge badge-blue">${item.status}</span></td>
                    <td>
                        ${isCreated ? `<button class="btn-action" onclick="handleGenerateVendorBill(${item.id})">Generate Bill</button>` : ''}
                        <button class="btn-delete" onclick="deletePO(${item.id})">Delete</button>
                    </td>
                </tr>
            `;
        }).join('') || `<tr><td colspan="8" style="text-align:center">No purchase orders found</td></tr>`;
    } catch (err) {
        console.error("Purchase Orders fetch error:", err);
    }
}

async function populateVendorDropdowns() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/vendors`);
        const vendors = await res.json();
        const select = document.getElementById("po-vendor");
        select.innerHTML = `<option value="">-- Select Vendor --</option>` +
            vendors.map(v => `<option value="${v.id}">${v.name}</option>`).join('');
    } catch (err) {
        console.error("Vendor dropdown error:", err);
    }
}

async function handleCreatePurchaseOrder(e) {
    e.preventDefault();
    const payload = {
        vendorId: parseInt(document.getElementById("po-vendor").value),
        item: document.getElementById("po-item").value,
        quantity: parseFloat(document.getElementById("po-qty").value),
        unitPrice: parseFloat(document.getElementById("po-price").value)
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/purchase-orders`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Purchase Order created successfully!");
            document.getElementById("form-purchaseOrder").reset();
            toggleForm("form-purchaseOrder");
            loadPurchaseOrders();
        }
    } catch (err) {
        showToast("Failed to create purchase order", "error");
    }
}

async function deletePO(id) {
    if (!confirm("Delete Purchase Order?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/purchase-orders/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Purchase Order deleted!");
            loadPurchaseOrders();
        }
    } catch (err) {
        showToast("Failed to delete purchase order", "error");
    }
}

async function handleGenerateVendorBill(poId) {
    try {
        const pos = await fetch(`${API_BASE_URL}/api/port/purchase-orders`).then(r => r.json());
        const po = pos.find(p => p.id === poId);
        if (!po) return;

        const billPayload = {
            purchaseOrderId: po.id,
            vendorId: po.vendorId,
            item: po.item,
            amount: po.totalAmount,
            status: "UNPAID"
        };

        const res = await fetch(`${API_BASE_URL}/api/port/vendor-bills`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(billPayload)
        });

        if (res.ok) {
            showToast("Vendor Bill & Expense Journal Entry created!");
            navigateTo("vendorBills");
        }
    } catch (err) {
        showToast("Failed to generate vendor bill", "error");
    }
}

// 9. VENDOR BILLS
async function loadVendorBills() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/vendor-bills`);
        const data = await res.json();
        const tbody = document.getElementById("table-vendorBills");
        tbody.innerHTML = data.map(item => {
            const isUnpaid = item.status === "UNPAID";
            const badgeClass = isUnpaid ? "badge-red" : "badge-green";
            return `
                <tr>
                    <td>BILL-${item.id}</td>
                    <td>PO-${item.purchaseOrderId || '-'}</td>
                    <td>VEND-${item.vendorId}</td>
                    <td><strong>${item.item || ''}</strong></td>
                    <td>₹${(item.amount ?? 0).toLocaleString()}</td>
                    <td><span class="badge ${badgeClass}">${item.status}</span></td>
                    <td>
                        ${isUnpaid ? `<button class="btn-action" onclick="handlePayVendorBill(${item.id})">Pay Bill</button>` : ''}
                        <button class="btn-delete" onclick="deleteVendorBill(${item.id})">Delete</button>
                    </td>
                </tr>
            `;
        }).join('') || `<tr><td colspan="7" style="text-align:center">No vendor bills recorded</td></tr>`;
    } catch (err) {
        console.error("Vendor Bills fetch error:", err);
    }
}

async function handlePayVendorBill(billId) {
    try {
        const payload = {
            vendorBillId: billId,
            paymentMethod: "Bank Transfer"
        };
        const res = await fetch(`${API_BASE_URL}/api/port/vendor-payments`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Vendor Payment recorded & Bank ledger updated!");
            loadVendorBills();
        }
    } catch (err) {
        showToast("Failed to record vendor payment", "error");
    }
}

async function deleteVendorBill(id) {
    if (!confirm("Delete Vendor Bill?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/vendor-bills/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Vendor Bill deleted!");
            loadVendorBills();
        }
    } catch (err) {
        showToast("Failed to delete vendor bill", "error");
    }
}

// 10. VENDOR PAYMENTS
async function loadVendorPayments() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/vendor-payments`);
        const data = await res.json();
        const tbody = document.getElementById("table-vendorPayments");
        tbody.innerHTML = data.map(item => `
            <tr>
                <td>PMT-${item.id}</td>
                <td>BILL-${item.vendorBillId}</td>
                <td>VEND-${item.vendorId}</td>
                <td><strong>₹${(item.amount ?? 0).toLocaleString()}</strong></td>
                <td>${item.paymentMethod || 'Bank'}</td>
                <td><span class="badge badge-green">${item.status}</span></td>
            </tr>
        `).join('') || `<tr><td colspan="6" style="text-align:center">No vendor payments recorded</td></tr>`;
    } catch (err) {
        console.error("Vendor Payments fetch error:", err);
    }
}

// 11. SALES ORDERS
async function loadSalesOrders() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/sales-orders`);
        const data = await res.json();
        const tbody = document.getElementById("table-salesOrders");
        tbody.innerHTML = data.map(item => {
            const isCreated = item.status === "CREATED";
            return `
                <tr>
                    <td>SO-${item.id}</td>
                    <td>SL-${item.shippingLineId}</td>
                    <td><strong>${item.service || ''}</strong></td>
                    <td>${item.quantity}</td>
                    <td>₹${item.unitPrice}</td>
                    <td><strong>₹${(item.totalAmount ?? 0).toLocaleString()}</strong></td>
                    <td><span class="badge badge-blue">${item.status}</span></td>
                    <td>
                        ${isCreated ? `<button class="btn-action" onclick="handleGenerateInvoiceFromSO(${item.id})">Create Invoice</button>` : ''}
                        <button class="btn-delete" onclick="deleteSO(${item.id})">Delete</button>
                    </td>
                </tr>
            `;
        }).join('') || `<tr><td colspan="8" style="text-align:center">No sales orders created</td></tr>`;
    } catch (err) {
        console.error("Sales Orders fetch error:", err);
    }
}

async function populateShippingLineDropdowns() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/shipping-lines`);
        const lines = await res.json();
        const soSelect = document.getElementById("so-shippingLine");
        if (soSelect) {
            soSelect.innerHTML = `<option value="">-- Select Shipping Line --</option>` +
                lines.map(l => `<option value="${l.id}">${l.name}</option>`).join('');
        }
        const invSelect = document.getElementById("inv-shippingLine");
        if (invSelect) {
            invSelect.innerHTML = `<option value="">-- Select Shipping Line --</option>` +
                lines.map(l => `<option value="${l.id}">${l.name}</option>`).join('');
        }
    } catch (err) {
        console.error("Shipping line dropdown error:", err);
    }
}

async function handleCreateSalesOrder(e) {
    e.preventDefault();
    const payload = {
        shippingLineId: parseInt(document.getElementById("so-shippingLine").value),
        service: document.getElementById("so-service").value,
        quantity: parseInt(document.getElementById("so-qty").value),
        unitPrice: parseFloat(document.getElementById("so-price").value)
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/sales-orders`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Sales Order created!");
            document.getElementById("form-salesOrder").reset();
            toggleForm("form-salesOrder");
            loadSalesOrders();
        }
    } catch (err) {
        showToast("Failed to create sales order", "error");
    }
}

async function deleteSO(id) {
    if (!confirm("Delete Sales Order?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/sales-orders/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Sales Order deleted!");
            loadSalesOrders();
        }
    } catch (err) {
        showToast("Failed to delete sales order", "error");
    }
}

async function handleGenerateInvoiceFromSO(soId) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/customer-invoices`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ salesOrderId: soId, demurrageAmount: 0 })
        });
        if (res.ok) {
            showToast("Customer Invoice & Revenue Journal Entry generated!");
            navigateTo("customerInvoices");
        }
    } catch (err) {
        showToast("Failed to generate invoice", "error");
    }
}

// 12. CUSTOMER INVOICES
async function loadCustomerInvoices() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/customer-invoices`);
        const data = await res.json();
        const tbody = document.getElementById("table-customerInvoices");
        tbody.innerHTML = data.map(item => {
            const isUnpaid = item.status === "UNPAID";
            const badgeClass = isUnpaid ? "badge-red" : "badge-green";
            return `
                <tr>
                    <td>INV-${item.id}</td>
                    <td>SO-${item.salesOrderId || '-'}</td>
                    <td>SL-${item.shippingLineId || '-'}</td>
                    <td>₹${(item.handlingAmount ?? 0).toLocaleString()}</td>
                    <td>₹${(item.demurrageAmount ?? 0).toLocaleString()}</td>
                    <td><strong>₹${(item.totalAmount ?? 0).toLocaleString()}</strong></td>
                    <td><span class="badge ${badgeClass}">${item.status}</span></td>
                    <td>
                        ${isUnpaid ? `<button class="btn-action" onclick="handleCollectCustomerPayment(${item.id})">Collect Payment</button>` : ''}
                        <button class="btn-delete" onclick="deleteInvoice(${item.id})">Delete</button>
                    </td>
                </tr>
            `;
        }).join('') || `<tr><td colspan="8" style="text-align:center">No customer invoices issued</td></tr>`;
    } catch (err) {
        console.error("Invoices fetch error:", err);
    }
}

async function handleCreateInvoice(e) {
    e.preventDefault();
    const payload = {
        shippingLineId: parseInt(document.getElementById("inv-shippingLine").value),
        handlingAmount: parseFloat(document.getElementById("inv-handling").value || 0),
        demurrageAmount: parseFloat(document.getElementById("inv-demurrage").value || 0)
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/customer-invoices`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Customer Invoice & Journal Entries generated!");
            document.getElementById("form-customerInvoice").reset();
            toggleForm("form-customerInvoice");
            loadCustomerInvoices();
        }
    } catch (err) {
        showToast("Failed to generate invoice", "error");
    }
}

async function handleCollectCustomerPayment(invId) {
    try {
        const payload = {
            customerInvoiceId: invId,
            paymentMethod: "Bank Wire Transfer"
        };
        const res = await fetch(`${API_BASE_URL}/api/port/customer-payments`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Customer Payment collected & Bank credited!");
            loadCustomerInvoices();
        }
    } catch (err) {
        showToast("Failed to collect payment", "error");
    }
}

async function deleteInvoice(id) {
    if (!confirm("Delete Invoice?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/customer-invoices/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Invoice deleted!");
            loadCustomerInvoices();
        }
    } catch (err) {
        showToast("Failed to delete invoice", "error");
    }
}

// 13. CUSTOMER PAYMENTS
async function loadCustomerPayments() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/customer-payments`);
        const data = await res.json();
        const tbody = document.getElementById("table-customerPayments");
        tbody.innerHTML = data.map(item => `
            <tr>
                <td>PMT-${item.id}</td>
                <td>INV-${item.customerInvoiceId}</td>
                <td>SL-${item.shippingLineId}</td>
                <td><strong>₹${(item.amount ?? 0).toLocaleString()}</strong></td>
                <td>${item.paymentMethod || 'Bank'}</td>
                <td><span class="badge badge-green">${item.status}</span></td>
            </tr>
        `).join('') || `<tr><td colspan="6" style="text-align:center">No customer payments received</td></tr>`;
    } catch (err) {
        console.error("Customer payments fetch error:", err);
    }
}

// 14. CHART OF ACCOUNTS
async function loadAccounts() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/accounts`);
        const data = await res.json();
        const tbody = document.getElementById("table-accounts");
        tbody.innerHTML = data.map(item => `
            <tr>
                <td>ACC-${item.id}</td>
                <td><strong>${item.accountName || ''}</strong></td>
                <td><span class="badge badge-blue">${item.accountType || ''}</span></td>
                <td>
                    <button class="btn-delete" onclick="deleteAccount(${item.id})">Delete</button>
                </td>
            </tr>
        `).join('') || `<tr><td colspan="4" style="text-align:center">No accounts defined</td></tr>`;
    } catch (err) {
        console.error("Accounts fetch error:", err);
    }
}

async function handleCreateAccount(e) {
    e.preventDefault();
    const payload = {
        accountName: document.getElementById("acc-name").value,
        accountType: document.getElementById("acc-type").value
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/accounts`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Account added to Chart of Accounts!");
            document.getElementById("form-account").reset();
            toggleForm("form-account");
            loadAccounts();
        }
    } catch (err) {
        showToast("Failed to create account", "error");
    }
}

async function deleteAccount(id) {
    if (!confirm("Delete Account?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/accounts/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Account deleted!");
            loadAccounts();
        }
    } catch (err) {
        showToast("Failed to delete account", "error");
    }
}

// 15. JOURNAL ENTRIES
async function loadJournalEntries() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/journal-entries`);
        const data = await res.json();
        const tbody = document.getElementById("table-journalEntries");
        tbody.innerHTML = data.map(item => `
            <tr>
                <td>JE-${item.id}</td>
                <td><span class="badge badge-gray">${item.entryType || 'GENERAL'}</span></td>
                <td><strong>${item.accountName || ''}</strong></td>
                <td style="color: ${item.debit > 0 ? '#10b981' : '#94a3b8'}">₹${(item.debit ?? 0).toLocaleString()}</td>
                <td style="color: ${item.credit > 0 ? '#0284c7' : '#94a3b8'}">₹${(item.credit ?? 0).toLocaleString()}</td>
                <td>${item.description || '-'}</td>
            </tr>
        `).join('') || `<tr><td colspan="6" style="text-align:center">No journal entries recorded</td></tr>`;
    } catch (err) {
        console.error("Journal Entries fetch error:", err);
    }
}

// 16. PROFIT & LOSS REPORT
async function loadProfitLossReport() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/reports/profit-loss`);
        if (res.ok) {
            const data = await res.json();
            document.getElementById("pl-handling").textContent = `₹${((data.totalRevenue || 0) * 0.7).toLocaleString()}`;
            document.getElementById("pl-demurrage").textContent = `₹${((data.totalRevenue || 0) * 0.3).toLocaleString()}`;
            document.getElementById("pl-totalRev").textContent = `₹${(data.totalRevenue || 0).toLocaleString()}`;
            document.getElementById("pl-expenses").textContent = `₹${(data.totalExpense || 0).toLocaleString()}`;
            document.getElementById("pl-totalExp").textContent = `₹${(data.totalExpense || 0).toLocaleString()}`;
            document.getElementById("pl-netProfit").textContent = `₹${(data.netProfit || 0).toLocaleString()}`;
        }
    } catch (err) {
        console.error("Profit Loss report error:", err);
    }
}

// 17. BALANCE SHEET REPORT
async function loadBalanceSheetReport() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/reports/balance-sheet`);
        if (res.ok) {
            const data = await res.json();
            document.getElementById("bs-assets").textContent = `₹${(data.totalAssets || 0).toLocaleString()}`;
            document.getElementById("bs-liabilities").textContent = `₹${(data.totalLiabilities || 0).toLocaleString()}`;
            document.getElementById("bs-equity").textContent = `₹${(data.totalEquity || 0).toLocaleString()}`;
        }
    } catch (err) {
        console.error("Balance sheet report error:", err);
    }
}

// 18. BUDGET / ANALYTIC ACCOUNTS
async function loadAnalyticAccounts() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/analytic-accounts`);
        const data = await res.json();
        const tbody = document.getElementById("table-analyticAccounts");
        tbody.innerHTML = data.map(item => `
            <tr>
                <td>AA-${item.id}</td>
                <td><strong>${item.accountName || ''}</strong></td>
                <td>₹${(item.budgetAmount ?? 0).toLocaleString()}</td>
                <td>₹${(item.actualAmount ?? 0).toLocaleString()}</td>
                <td>
                    <button class="btn-delete" onclick="deleteAnalyticAccount(${item.id})">Delete</button>
                </td>
            </tr>
        `).join('') || `<tr><td colspan="5" style="text-align:center">No analytic accounts configured</td></tr>`;
    } catch (err) {
        console.error("Analytic accounts fetch error:", err);
    }
}

async function handleCreateAnalyticAccount(e) {
    e.preventDefault();
    const payload = {
        accountName: document.getElementById("analytic-name").value,
        budgetAmount: parseFloat(document.getElementById("analytic-budget").value),
        actualAmount: 0.0
    };
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/analytic-accounts`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast("Analytic Account created!");
            document.getElementById("form-analytic").reset();
            toggleForm("form-analytic");
            loadAnalyticAccounts();
        }
    } catch (err) {
        showToast("Failed to create analytic account", "error");
    }
}

async function deleteAnalyticAccount(id) {
    if (!confirm("Delete Analytic Account?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/analytic-accounts/${id}`, { method: "DELETE" });
        if (res.ok) {
            showToast("Analytic Account deleted!");
            loadAnalyticAccounts();
        }
    } catch (err) {
        showToast("Failed to delete analytic account", "error");
    }
}

// 19. BUDGET VARIANCE
async function loadBudgetVariance() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/port/budget-variance`);
        const data = await res.json();
        const tbody = document.getElementById("table-budgetVariance");
        tbody.innerHTML = data.map(item => {
            const variance = item.varianceAmount ?? (item.budgetAmount - item.actualAmount);
            const isFavorable = variance >= 0;
            const badgeClass = isFavorable ? "badge-green" : "badge-red";
            return `
                <tr>
                    <td>VAR-${item.id}</td>
                    <td><strong>${item.accountName || ''}</strong></td>
                    <td>₹${(item.budgetAmount ?? 0).toLocaleString()}</td>
                    <td>₹${(item.actualAmount ?? 0).toLocaleString()}</td>
                    <td><strong>₹${variance.toLocaleString()}</strong></td>
                    <td><span class="badge ${badgeClass}">${isFavorable ? 'Favorable' : 'Over Budget'}</span></td>
                </tr>
            `;
        }).join('') || `<tr><td colspan="6" style="text-align:center">No budget variance items calculated</td></tr>`;
    } catch (err) {
        console.error("Budget variance fetch error:", err);
    }
}

// ==========================================================================
// AUTHENTICATION & SESSION MANAGEMENT
// ==========================================================================

let currentUser = null;

function initAuth() {
    const savedUser = localStorage.getItem("portops_user");
    if (savedUser) {
        try {
            currentUser = JSON.parse(savedUser);
            renderHeaderUserProfile(currentUser);
            hideAuthScreen();
            navigateTo("dashboard");
            return;
        } catch (e) {
            console.error("Failed to parse saved user session", e);
            localStorage.removeItem("portops_user");
        }
    }
    showAuthScreen();
}

function showAuthScreen() {
    const authScreen = document.getElementById("auth-screen");
    if (authScreen) authScreen.classList.remove("hidden");
    const appEl = document.querySelector(".app");
    if (appEl) appEl.style.display = "none";
}

function hideAuthScreen() {
    const authScreen = document.getElementById("auth-screen");
    if (authScreen) authScreen.classList.add("hidden");
    const appEl = document.querySelector(".app");
    if (appEl) appEl.style.display = "flex";
}

function switchAuthTab(tab) {
    const tabLogin = document.getElementById("tab-login");
    const tabReg = document.getElementById("tab-register");
    const formLogin = document.getElementById("form-login");
    const formReg = document.getElementById("form-register");
    const alertBox = document.getElementById("auth-alert");

    alertBox.className = "auth-alert hidden";

    if (tab === "login") {
        tabLogin.classList.add("active");
        tabReg.classList.remove("active");
        formLogin.classList.remove("hidden");
        formReg.classList.add("hidden");
    } else {
        tabReg.classList.add("active");
        tabLogin.classList.remove("active");
        formReg.classList.remove("hidden");
        formLogin.classList.add("hidden");
    }
}

function fillDemoUser(username, password) {
    switchAuthTab("login");
    document.getElementById("login-username").value = username;
    document.getElementById("login-password").value = password;
    showAuthAlert(`Demo credentials loaded for '${username}'`, "success");
}

function showAuthAlert(msg, type = "error") {
    const alertBox = document.getElementById("auth-alert");
    if (!alertBox) return;
    alertBox.textContent = msg;
    alertBox.className = `auth-alert ${type}`;
}

async function handleLoginSubmit(event) {
    event.preventDefault();
    const username = document.getElementById("login-username").value.trim();
    const password = document.getElementById("login-password").value.trim();

    if (!username || !password) {
        showAuthAlert("Please enter both username and password.");
        return;
    }

    const btn = document.getElementById("login-submit-btn");
    const originalBtnHtml = btn.innerHTML;
    btn.innerHTML = `<span>Signing In...</span>`;
    btn.disabled = true;

    try {
        const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        const data = await res.json();

        if (res.ok && data.success) {
            currentUser = data.user;
            localStorage.setItem("portops_user", JSON.stringify(currentUser));
            showAuthAlert("Login successful! Redirecting...", "success");
            setTimeout(() => {
                renderHeaderUserProfile(currentUser);
                hideAuthScreen();
                navigateTo("dashboard");
                showToast(`Welcome back, ${currentUser.fullName || currentUser.username}!`);
                btn.innerHTML = originalBtnHtml;
                btn.disabled = false;
            }, 600);
            return;
        } else {
            showAuthAlert(data.message || "Invalid credentials.");
        }
    } catch (err) {
        console.warn("Backend auth unavailable, performing client-side validation check", err);
        const demoAccounts = {
            "admin": { id: 1, username: "admin", password: "admin123", fullName: "Capt. Alex Mercer", role: "ADMIN", department: "Terminal Command" },
            "yardmanager": { id: 2, username: "yardmanager", password: "yard123", fullName: "Marcus Vance", role: "YARD_MANAGER", department: "Yard Logistics" },
            "finance": { id: 3, username: "finance", password: "finance123", fullName: "Elena Rostova", role: "FINANCE_OFFICER", department: "Finance & Accounts" },
            "shipping": { id: 4, username: "shipping", password: "shipping123", fullName: "Sarah Jenkins", role: "SHIPPING_LINE_AGENT", department: "Shipping Ops" }
        };

        const found = demoAccounts[username.toLowerCase()];
        if (found && found.password === password) {
            currentUser = { id: found.id, username: found.username, fullName: found.fullName, role: found.role, department: found.department };
            localStorage.setItem("portops_user", JSON.stringify(currentUser));
            showAuthAlert("Login successful! Redirecting...", "success");
            setTimeout(() => {
                renderHeaderUserProfile(currentUser);
                hideAuthScreen();
                navigateTo("dashboard");
                showToast(`Welcome back, ${currentUser.fullName}!`);
                btn.innerHTML = originalBtnHtml;
                btn.disabled = false;
            }, 600);
            return;
        } else {
            showAuthAlert("Invalid username or password.");
        }
    }

    btn.innerHTML = originalBtnHtml;
    btn.disabled = false;
}

async function handleRegisterSubmit(event) {
    event.preventDefault();
    const fullName = document.getElementById("reg-fullname").value.trim();
    const email = document.getElementById("reg-email").value.trim();
    const username = document.getElementById("reg-username").value.trim();
    const password = document.getElementById("reg-password").value.trim();
    const role = document.getElementById("reg-role").value;

    if (!username || !password || !fullName || !email) {
        showAuthAlert("Please fill in all registration fields.");
        return;
    }

    const btn = document.getElementById("reg-submit-btn");
    const originalBtnHtml = btn.innerHTML;
    btn.innerHTML = `<span>Creating Account...</span>`;
    btn.disabled = true;

    try {
        const res = await fetch(`${API_BASE_URL}/api/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ fullName, email, username, password, role })
        });
        const data = await res.json();

        if (res.ok && data.success) {
            currentUser = data.user;
            localStorage.setItem("portops_user", JSON.stringify(currentUser));
            showAuthAlert("Account created successfully!", "success");
            setTimeout(() => {
                renderHeaderUserProfile(currentUser);
                hideAuthScreen();
                navigateTo("dashboard");
                showToast(`Account registered! Welcome, ${currentUser.fullName}!`);
                btn.innerHTML = originalBtnHtml;
                btn.disabled = false;
            }, 600);
            return;
        } else {
            showAuthAlert(data.message || "Registration failed.");
        }
    } catch (err) {
        console.warn("Backend register unavailable, creating local demo account session", err);
        currentUser = { id: Date.now(), username, fullName, email, role, department: "Operations" };
        localStorage.setItem("portops_user", JSON.stringify(currentUser));
        showAuthAlert("Account created! Redirecting...", "success");
        setTimeout(() => {
            renderHeaderUserProfile(currentUser);
            hideAuthScreen();
            navigateTo("dashboard");
            showToast(`Account created! Welcome, ${fullName}!`);
            btn.innerHTML = originalBtnHtml;
            btn.disabled = false;
        }, 600);
        return;
    }

    btn.innerHTML = originalBtnHtml;
    btn.disabled = false;
}

function renderHeaderUserProfile(user) {
    if (!user) return;
    const nameEl = document.getElementById("header-user-name");
    const roleEl = document.getElementById("header-user-role");
    const avatarEl = document.getElementById("header-user-avatar");

    if (nameEl) nameEl.textContent = user.fullName || user.username || "Operator";
    if (roleEl) roleEl.textContent = user.role || "USER";

    if (avatarEl) {
        const nameParts = (user.fullName || user.username || "U").split(" ");
        const initials = nameParts.length >= 2 ? (nameParts[0][0] + nameParts[1][0]).toUpperCase() : nameParts[0].substring(0, 2).toUpperCase();
        avatarEl.textContent = initials;
    }
}

function handleLogout() {
    localStorage.removeItem("portops_user");
    currentUser = null;
    showToast("Logged out from PortOps Terminal session.", "error");
    showAuthScreen();
}

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => {
    initAuth();
});