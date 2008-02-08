Ext.BLANK_IMAGE_URL = './images/gray/s.gif';
Ext.onReady(function(){
	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
	
	function createGrid(id, title, data) {
		var gp = new Ext.grid.GridPanel({
			store: new Ext.data.SimpleStore({
				data: data,
				fields: [
					{name: 'key'},
					{name: 'value'}
				]
			}),
			columns: [
				{id:'key',header: "Key", width: 100, sortable: true, dataIndex: 'key'},
				{id:'value',header: "Value", width: 50, sortable: true, dataIndex: 'value'}
			],
			tbar:[
				new Ext.form.TextField({
					width: 200,
					emptyText:'Filter Key',
					listeners:{
						render: function(f){
							f.el.on('keydown', filterKey, f, {buffer: 350});
						}
					}
				})
			],
			stripeRows: true,
			autoExpandColumn: 'value',
			id:id,
			title: title,
			closable:true,
			autoScroll:true
		});
		function filterKey(e) {
			var text = e.target.value;
			gp.store.filter('key',text,true,true);
		}
		return gp;
	}
	
	function convertStackTrace() {
		var row = $('div#stacktrace > pre').text().split('\n');
		var title = row.shift();
		var len = row.length;
		var datas = new Array();
		for(var i=0; i<len;i++) {
			var spi = row[i].indexOf('(');
			var epi = row[i].indexOf(')');
			var method = row[i].substring(row[i].indexOf('at ')+3, spi);
			var clazz = row[i].substring(spi+1, epi);
			var line = 0;
			var ci = clazz.indexOf(':');
			if(1 < ci) {
				line = clazz.substring(ci+1);
				clazz = clazz.substring(0,ci);
			}
			if(method != null && 0 < method.length){
				datas.push([method,clazz,line]);
			}
		}
		return {title: title, data:datas};
	}
	
	function createStackTraceGrid() {
		var datas = convertStackTrace();
		var gp = new Ext.grid.GridPanel({
			store: new Ext.data.SimpleStore({
				data: datas.data,
				fields: [
					{name: 'method'},
					{name: 'clazz'},
					{name: 'line'}
				]
			}),
			columns: [
				{id:'method',header: 'Method' , width: 100, sortable: false, dataIndex: 'method'},
				{id:'clazz' ,header: 'Class'  , width: 100, sortable: false, dataIndex: 'clazz'},
				{id:'line'  ,header: 'Line No', width: 50,  sortable: false, dataIndex: 'line'}
			],
			viewConfig: {
				forceFit: true
			},
			resizeTabs:true,
			autoExpandColumn: 'method',
			id:id.title + '-xid',
			title: datas.title,
			autoScroll:true
		});
		gp.on('rowdblclick',function(){
				// ResourceSyncronizerにHTTPリクエストを投げる感じ。
		});
		return gp;
	}
	
	var mainArea = new Ext.TabPanel({
				region:'center',
				deferredRender:false,
				activeTab:0,
				enableTabScroll:true,
				plugins: new Ext.ux.TabCloseMenu(),
				items:[
					{
						title: 'StackTrace',
						autoScroll:true,
						items:[
							createStackTraceGrid()
						]
					}
				]
	});
	

	function createClickHandler(id, title, dataloader) {
		return function(){
			if(mainArea.findById(id) == null) {
				mainArea.add(createGrid(id, title, dataloader()));
			}
			mainArea.activate(id);
		};
	}
	
	function createNode(id, text) {
		return new Ext.tree.TreeNode({
			id:id,
			text:text
		});
	}
	
	function onClick(node, loader) {
		node.on('click',createClickHandler(node.id+'-grid', node.text, loader));
	}
	
	var menuTree = new Ext.tree.TreePanel({
				region:'west',
				el:'menu',
				id:'menu-panel',
				title:'Menu',
				width: 200,
				minSize: 175,
				maxSize: 400,
				rootVisible: false,
				collapsible: true,
				margins:'0 0 0 5',
				animate:true,
				autoScroll:true,
				containerScroll: true
	});
	var rootNode = createNode('source','Debug Informations');

	var http = createNode('http','HTTP');
	onClick(http, debuginfo.loadbasic);
	var headers = createNode('httpheader','HTTP Headers');
	onClick(headers, debuginfo.loadHttpHeader);
	var params = createNode('requestparams','Request Parameters');
	onClick(params, debuginfo.loadRequestParam);
	var cookie = createNode('cookies','Cookie');
	onClick(cookie, debuginfo.loadCookie);
	
	http.appendChild([params,headers,cookie]);
	
	var javaNode = createNode('java','Java');
	javaNode.on('click',createClickHandler('javaSystemProps','Java System Properties',debuginfo.loadJavaSystemProps));
	var request = createNode('requestAttributes','Request Attributes');
	onClick(request, debuginfo.loadRequestAttr);
	var session = createNode('sessionAttributes','Session Attributes');
	onClick(session, debuginfo.loadSessionAttr);
	var app = createNode('applicationAttributes','Application Attributes');
	onClick(app, debuginfo.loadAppAttr);
	
	javaNode.appendChild([request,session,app]);
	
	rootNode.appendChild([http,javaNode]);
	menuTree.setRootNode(rootNode);
	
	var viewport = new Ext.Viewport({
		layout:'border',
		items:[
			new Ext.BoxComponent({ // raw
				region:'north',
				el: 'top',
				height:32
			}),menuTree,mainArea
			
		]
	});
	menuTree.expandAll();
});