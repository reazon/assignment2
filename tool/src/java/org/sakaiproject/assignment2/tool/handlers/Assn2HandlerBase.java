package org.sakaiproject.assignment2.tool.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.sdata.tool.json.JSONServiceHandler;

public abstract class Assn2HandlerBase extends JSONServiceHandler
{
	@Override
	public final void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		handleDelete(request, response);
	}

	@Override
	public final void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		handleGet(request, response);
	}

	@Override
	public final void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		handleHead(request, response);
	}

	@Override
	public final void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		handlePost(request, response);
	}

	@Override
	public final void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		handlePut(request, response);
	}

	@Override
	public final void init(Map<String, String> config) throws ServletException
	{
		postInit(config);
	}

	public void handleDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public void handleGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public void handleHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public void handlePost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public void handlePut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public void postInit(Map<String, String> config) throws ServletException
	{
		
	}
}