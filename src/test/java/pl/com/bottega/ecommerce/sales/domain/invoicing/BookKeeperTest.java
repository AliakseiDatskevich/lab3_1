package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class BookKeeperTest {

	Money money;
	InvoiceFactory invoiceFactory;
	BookKeeper bookKeeper;
	ProductData productData;
	RequestItem requestItem;
	InvoiceRequest mockedInvoiceRequest;
	TaxPolicy mockedTaxPolicy;
	Tax tax;

	@Before
	public void setUp() {
		money = new Money(10);
		invoiceFactory = new InvoiceFactory();
		bookKeeper = new BookKeeper(invoiceFactory);
		productData = new ProductData(new Id("1"), money, "Product", ProductType.STANDARD, new Date());
		requestItem = new RequestItem(productData, 1, money);
		tax = new Tax(new Money(2.3), "Tax");
		mockedInvoiceRequest = mock(InvoiceRequest.class);
		mockedTaxPolicy = mock(TaxPolicy.class);

	}

	@Test
	public void requestingAnInvoiceWithOneItemIsCorrect() {
		when(mockedInvoiceRequest.getItems()).thenReturn(Arrays.asList(requestItem));
		when(mockedTaxPolicy.calculateTax(ProductType.STANDARD, requestItem.getTotalCost())).thenReturn(tax);

		Invoice result = bookKeeper.issuance(mockedInvoiceRequest, mockedTaxPolicy);

		assertThat(result.getItems().size(), Matchers.is(1));
	}

	@Test
	public void requestingAnInvoiceWithTwoItemsShouldCallTheCalculateTaxMethodTwice() {
		when(mockedInvoiceRequest.getItems()).thenReturn(Arrays.asList(requestItem, requestItem));
		when(mockedTaxPolicy.calculateTax(ProductType.STANDARD, requestItem.getTotalCost())).thenReturn(tax);

		bookKeeper.issuance(mockedInvoiceRequest, mockedTaxPolicy);

		verify(mockedTaxPolicy, times(2)).calculateTax(ProductType.STANDARD, requestItem.getTotalCost());
	}
}
