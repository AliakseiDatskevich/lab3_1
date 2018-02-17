package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class BookKeeperNewTest {

	private BookKeeper bookKeeper;
	private InvoiceRequest invoiceRequest;
	private TaxPolicy taxPolicy;

	@Before
	public void setUp() {
		bookKeeper = new BookKeeper(new InvoiceFactory());
		invoiceRequest = mock(InvoiceRequest.class);
		taxPolicy = mock(TaxPolicy.class);
		Tax tax = new Tax(new Money(23, Money.DEFAULT_CURRENCY), "VAT");
		doReturn(tax).when(taxPolicy).calculateTax(any(ProductType.class), any(Money.class));
	}

	@Test
	public void testIssuanceSingleItem() {
		List<RequestItem> requestItems = new ArrayList<>();
		ProductData productData = mock(ProductData.class);
		requestItems.add(new RequestItem(productData, 2, new Money(10)));
		doReturn(requestItems).when(invoiceRequest).getItems();
		
		Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
		
		assertEquals(1, invoice.getItems().size());
	}
	
	@Test
	public void testIssuanceTwoItems() {
		List<RequestItem> requestItems = new ArrayList<>();
		ProductData productData = mock(ProductData.class);
		requestItems.add(new RequestItem(productData, 2, new Money(10)));
		requestItems.add(new RequestItem(productData, 3, new Money(20)));
		doReturn(requestItems).when(invoiceRequest).getItems();
		
		Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
		
		assertEquals(2, invoice.getItems().size());
	}
}
