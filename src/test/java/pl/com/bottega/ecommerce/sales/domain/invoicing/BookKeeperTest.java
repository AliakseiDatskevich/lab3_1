package pl.com.bottega.ecommerce.sales.domain.invoicing;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Lukasz on 2018-01-28.
 */
@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    private InvoiceFactory invoiceFactory = new InvoiceFactory();
    ;
    private InvoiceRequest invoiceRequest;
    private ClientData clientData = new ClientData(Id.generate(), "john doe");
    @Mock
    private ProductData productData;
    @Mock
    private TaxPolicy taxPolicy;

    @Test
    public void invoiceRequestWithOneItemReturnInvoiceWithOneItem() {
        invoiceRequest = new InvoiceRequest(clientData);

        invoiceRequest.add(new RequestItem(productData, 1, new Money(7)));
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(7), ""));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getItems().size(), is(1));
    }

}