/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation;

import java.util.List;

/**
 *
 * @author marx
 */
public interface Strategy<T> {

	List<Item> calculate(final T item_id);
	
}
