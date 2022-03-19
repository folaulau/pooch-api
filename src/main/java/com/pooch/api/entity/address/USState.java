package com.pooch.api.entity.address;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum USState {

	// @formatter:off
    ALABAMA("Alabama", "AL"),
    ALASKA("Alaska", "AK"),
    ARIZONA("Arizona", "AZ"),
    ARKANSAS("Arkansas", "AR"),
    CALIFORNIA("California", "CA"),
    COLORADO("Colorado", "CO"),
    CONNECTICUT("Connecticut", "CT"),
    DELAWARE("Delaware", "DE"),
    FLORIDA("Florida", "FL"),
    GEORGIA("Georgia", "GA"),
    HAWAII("Hawaii", "HI"),
    IDAHO("Idaho", "ID"),
    ILLINOIS("Illinois", "IL"),
    INDIANA("Indiana", "IN"),
    IOWA("Iowa", "IA"),
    KANSAS("Kansas", "KS"),
    KENTUCKY("Kentucky", "KY"),
    LOUISIANA("Louisiana", "LA"),
    MAINE("Maine", "ME"),
    MARYLAND("Maryland	", "MD"),
    MASSACHUSETTS("Massachusetts", "MA"),
    MICHIGAN("Michigan", "MI"),
    MINNESOTA("Minnesota", "MN"),
    MISSISSIPPI("Mississippi", "MS"),
    MISSOURI("Missouri", "MO"),
    MONTANA("Montana", "MT"),
    NEBRASKA("Nebraska", "NE"),
    NEVADA("Nevada", "NV"),
    NEW_HAMPSHIRE("New Hampshire", "NH"),
    NEW_JERSEY("New Jersey", "NJ"),
    NEW_MEXICO("New Mexico", "NM"),
    NEW_YORK("New York", "NY"),
    NORTH_CAROLINA("North Carolina", "NC"),
    NORTH_DAKOTA("North Dakota", "ND"),
    OHIO("Ohio", "OH"),
    OKLAHOMA("Oklahoma", "OK"),
    OREGON("Oregon", "OR"),
    PENNSYLVANIA("Pennsylvania", "PA"),
    RHODE_ISLAND("Rhode Island", "RI"),
    SOUTH_CAROLINA("South Carolina", "SC"),
    SOUTH_DAKOTA("South Dakota", "SD"),
    TENNESSEE("Tennessee", "TN"),
    TEXAS("Texas", "TX"),
    UTAH("Utah", "UT"),
    VERMONT("Vermont", "VT"),
    VIRGINIA("Virginia", "VA"),
    VIRGIN_ISLANDS("Virgin Islands","VI"),
    WASHINGTON("Washington", "WA"),
    WEST_VIRGINIA("West Virginia", "WV"),
    WISCONSIN("Wisconsin", "WI"),
    WYOMING("Wyoming", "WY"),
	NONE("NONE", "NONE");
	// @formatter:on

	String name;

	String abbreviation;

	USState(String name, String abbreviation) {
		this.name = name;
		this.abbreviation = abbreviation;
	}

	public String getName() {
		return name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * prod = production <br>
	 * dev = development <br>
	 * qa = QA <br>
	 * 
	 * @param state
	 * @param env
	 * @return
	 */
	public static boolean isApprovedState(String state, String env) {
		if (state == null || state.isEmpty()) {
			return false;
		}

		return (env != null && env.equals("prod")) ? isApprovedStateInProd(state) : isApprovedStateInNonProd(state);
	}

	private static boolean isApprovedStateInNonProd(String state) {
		if (state == null) {
			return false;
		}

		// List of states that are approved
		List<USState> approvedStates = new ArrayList<USState>();
		approvedStates.add(USState.UTAH);

		boolean status = false;

		for (USState st : approvedStates) {
			if (st.getAbbreviation().toUpperCase().equals(state.toUpperCase())) {
				return true;
			}
		}

		return status;
	}

	private static boolean isApprovedStateInProd(String state) {

		if (state == null) {
			return false;
		}

		List<USState> approvedStates = new ArrayList<USState>();
		approvedStates.add(USState.UTAH);

		boolean status = false;

		for (USState st : approvedStates) {
			if (st.getAbbreviation().toUpperCase().equals(state.toUpperCase())) {
				return true;
			}
		}

		return status;
	}

	public static String getStateAbbreviation(String st) {

		if (st == null) {
			return null;
		}
		// look for abbrev
		for (USState state : USState.values()) {
			if (state.getAbbreviation().toUpperCase().equals(st.toUpperCase())) {
				return state.abbreviation;
			}
		}
		// look for name
		for (USState state : USState.values()) {
			if (state.getName().toUpperCase().equals(st.toUpperCase())) {
				return state.abbreviation;
			}
		}

		return null;
	}

	/**
	 * Given abbreviation
	 */
	public static boolean isValidUSState(String abbrev) {
		for (USState state : USState.values()) {
			if (state.getName().toUpperCase().equals(abbrev.toUpperCase())
					|| state.getAbbreviation().toUpperCase().equals(abbrev.toUpperCase())) {
				return true;
			}
		}

		return false;
	}
	
	public static USState getStateTypeFromStr(String str) {
		if (str == null || str.length() == 0) {
			return NONE;
		}

		return Arrays.asList(USState.values()).stream()
				.filter(licen -> licen.name().toLowerCase().equals(str.toLowerCase()))
				.findFirst().orElse(NONE);
	}
	
}
