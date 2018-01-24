package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class BookKeeperTest {

	@Test
	public void requestingAnInvoiceWithOneItemIsCorrect() {
		Money money = new Money(10);
		InvoiceFactory invoiceFactory = new InvoiceFactory();
		BookKeeper bookKeeper = new BookKeeper(invoiceFactory);
		ProductData productData = new ProductData(new Id("1"), money, "Product", ProductType.STANDARD, new Date());
		RequestItem requestItem = new RequestItem(productData, 1, money);
		InvoiceRequest mockedInvoiceRequest = mock(InvoiceRequest.class);
		TaxPolicy mockedTaxPolicy = mock(TaxPolicy.class);

		when(mockedInvoiceRequest.getItems()).thenReturn(Arrays.asList(requestItem));
		when(mockedTaxPolicy.calculateTax(ProductType.STANDARD, requestItem.getTotalCost()))
				.thenReturn(new Tax(new Money(2.3), "Tax"));

		Invoice result = bookKeeper.issuance(mockedInvoiceRequest, mockedTaxPolicy);

		assertThat(result.getItems().size(), Matchers.is(1));
	}

}
