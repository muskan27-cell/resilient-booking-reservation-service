const API_BASE = "http://localhost:8080";

const inventorySelect = document.querySelector("#inventorySelect");
const inventoryList = document.querySelector("#inventoryList");
const bookingsList = document.querySelector("#bookingsList");
const bookingForm = document.querySelector("#bookingForm");
const responseBox = document.querySelector("#responseBox");
const idempotencyInput = document.querySelector("#idempotencyInput");
const emailInput = document.querySelector("#emailInput");
const quantityInput = document.querySelector("#quantityInput");
const forceFailureInput = document.querySelector("#forceFailureInput");

let lastPayload = null;

function money(cents) {
  return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(cents / 100);
}

function newKey() {
  idempotencyInput.value = `booking-${crypto.randomUUID()}`;
}

async function fetchJson(path, options) {
  const response = await fetch(`${API_BASE}${path}`, options);
  const text = await response.text();
  try {
    return { status: response.status, body: JSON.parse(text) };
  } catch {
    return { status: response.status, body: text };
  }
}

async function loadInventory() {
  const { body } = await fetchJson("/api/inventory");
  inventorySelect.innerHTML = body
    .map((item) => `<option value="${item.id}">${item.name} (${item.available} left)</option>`)
    .join("");
  inventoryList.innerHTML = body
    .map((item) => `
      <article class="item">
        <strong>${item.name}</strong>
        <span class="meta">${item.type} · ${item.sku}</span>
        <span class="meta">${item.available} available · ${money(item.priceCents)} · version ${item.version}</span>
      </article>
    `)
    .join("");
}

async function loadBookings() {
  const { body } = await fetchJson("/api/bookings");
  bookingsList.innerHTML = body.length
    ? body.toReversed().map((booking) => `
      <article class="row">
        <strong>${booking.customerEmail} · ${money(booking.amountCents)}</strong>
        <span class="status ${booking.status}">${booking.status}</span>
        <span class="meta">Booking ${booking.id} · item ${booking.inventoryItemId} · quantity ${booking.quantity}</span>
        ${booking.failureReason ? `<span class="meta">${booking.failureReason}</span>` : ""}
      </article>
    `).join("")
    : `<p class="meta">No bookings yet.</p>`;
}

async function refresh() {
  try {
    await loadInventory();
    await loadBookings();
  } catch (error) {
    responseBox.textContent = JSON.stringify({ error: "API unreachable", detail: error.message }, null, 2);
  }
}

async function submitBooking(payload = null) {
  const request = payload ?? {
    inventoryItemId: Number(inventorySelect.value),
    customerEmail: emailInput.value,
    quantity: Number(quantityInput.value),
    forcePaymentFailure: forceFailureInput.checked
  };
  lastPayload = request;

  const result = await fetchJson("/api/bookings", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Idempotency-Key": idempotencyInput.value
    },
    body: JSON.stringify(request)
  });

  responseBox.textContent = JSON.stringify(result, null, 2);
  await refresh();
}

bookingForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  await submitBooking();
});

document.querySelector("#retryBtn").addEventListener("click", async () => {
  if (lastPayload) {
    await submitBooking(lastPayload);
  }
});

document.querySelector("#newKeyBtn").addEventListener("click", newKey);
document.querySelector("#refreshBtn").addEventListener("click", refresh);

newKey();
refresh();
