/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.wicket.util.crypt.StringUtils;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class CollectionTest {

	@Test
	public void collection_test () {
		    var collection1 = List.of(1, 2, 3, 4, 5);
    var collection2 = List.of(2, 3, 5, 6);
    System.out.println(collection1);
    System.out.println(collection2);
    System.out.println(CollectionUtils.subtract(collection1, collection2));
    System.out.println(CollectionUtils.retainAll(collection1, collection2));
    System.out.println(CollectionUtils.collate(collection1, collection2));
    System.out.println(CollectionUtils.disjunction(collection1, collection2));
    System.out.println(CollectionUtils.intersection(collection1, collection2));
    System.out.println(CollectionUtils.union(collection1, collection2));
	}
}
