import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.invoicing.*;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

public class BookKeeperTest {

    private InvoiceFactory invoiceFactory;
    private TaxPolicy taxPolicy;
    private Money AMOUNT = new Money(10);
    private ProductData productData;
    private InvoiceRequest invoiceRequest;
    private ClientData clientData = new ClientData(Id.generate(), "Andrzej");

    @Before public void setUp() {
        invoiceFactory = new InvoiceFactory();
        productData = mock(ProductData.class);
        taxPolicy = mock(TaxPolicy.class);
        invoiceRequest = new InvoiceRequest(clientData);
    }

    @Test public void testIssuanceRequestToReturnIninvoiceWithOneItem() {
        invoiceRequest.add(new RequestItem(productData, 1, AMOUNT));
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(AMOUNT, "Default desc"));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getItems().size(), is(1));

    }

    @Test public void testIssuanceBehaviorCalculateTaxMethodCallTwoTimes() {
        TaxPolicy taxPolicy = spy(TaxPolicy.class);
        ProductData productData = new ProductData(Id.generate(), AMOUNT, "name product", ProductType.DRUG, new Date());
        ProductData productData1 = new ProductData(Id.generate(), AMOUNT, "name product1", ProductType.FOOD,
                new Date());

        invoiceRequest.add(new RequestItem(productData, 1, AMOUNT));
        invoiceRequest.add(new RequestItem(productData1, 1, AMOUNT));
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(AMOUNT, "desc"));
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(any(ProductType.class), any(Money.class));

    }

    @Test public void testIssuanceInvoiceWithTheSameItem() {
        ProductData p = new ProductData(Id.generate(), AMOUNT, "name product", ProductType.DRUG, new Date());
        RequestItem requestItem = new RequestItem(p, 1, AMOUNT);
        int NUMBER_ITEM = 150;
        for (int i = 0; i < NUMBER_ITEM; i++) {
            invoiceRequest.add(requestItem);
        }

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(AMOUNT, "desc"));

        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getItems().size(), is(150));
    }

    @Test public void testIssuanceCheckNeverCallCalculateTax() {
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(AMOUNT, "desc"));

        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, never()).calculateTax(any(ProductType.class), any(Money.class));
    }

}
