<html><head>
	<title>Rich Error Screen For Servlet/JSP</title>
	<link rel="stylesheet" type="text/css" href="./css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./css/xtheme-gray.css" />
	<script type="text/javascript" src="./js/jquery-1.2.2.min.js"></script>
	<script type="text/javascript" src="./js/ext-base.js"></script>
	<script type="text/javascript" src="./js/ext-all.js"></script>
	<script type="text/javascript" src="./js/TabCloseMenu.js"></script>
	<style type="text/css">
	html, body {
		font:normal 12px verdana;
		margin:0;
		padding:0;
		border:0 none;
		overflow:hidden;
		height:100%;
	}
	p {
		margin:5px;
	}
	</style>
	<script type="text/javascript">
		var debuginfo = {};
		debuginfo.loadbasic = function() {
			return [
				['ContextPath','<%= request.getContextPath() %>'],
				['RequestURI','<%= request.getRequestURI() %>'],
				['RequestMethod','<%= request.getMethod() %>']
			];
		};
		debuginfo.loadRequestParam = function() {
				return [
					['aaa0','bbb0'],
					['aaa1','bbb1'],
					['aaa2','bbb2'],
					['aaa3','bbb3']
				];
		};
		debuginfo.loadHttpHeader = function() {
				return [
					['aaa0','bbb0'],
					['aaa1','bbb1'],
					['aaa2','bbb2'],
					['aaa3','bbb3']
				];
		};
		debuginfo.loadRequestAttr = function() {
				return [
					['aaa00','bbb00'],
					['aaa11','bbb11'],
					['aaa22','bbb22'],
					['aaa33','bbb33']
				];
		};
		debuginfo.loadSessionAttr = function() {
				return [
					['aaaz00','bbbz00'],
					['aaaz11','bbbz11'],
					['aaaz22','bbbz22'],
					['aaaz33','bbbz33']
				];
		};
		debuginfo.loadCookie = function() {
			var result = new Array();
			var ary = document.cookie.split(';');
			var len = ary.length;
			for(var i=0; i<len; i++) {
				var kv = ary[i].split('=');
				kv[1] = unescape(kv[1]);
				result.push(kv);
			}
			return result;
		}
		debuginfo.loadAppAttr = function() {
				return [
					['aaa000','bbb000'],
					['aaa111','bbb111'],
					['aaa222','bbb222'],
					['aaa333','bbb333']
				];
		};
		debuginfo.loadJavaSystemProps = function() {
				return [
					['aaa000','bbb000'],
					['aaa111','bbb111'],
					['aaa222','bbb222'],
					['aaa3333333333333333333333333333333333333333333zzz','bbb333']
				];
		};
	</script>
	<script type="text/javascript" src="./js/debug-info.js"></script>
</head>
<body>
	<div id="top">
		<p><b>Rich Error Screen For Servlet/JSP</b></p>
	</div>
<div id="menu"></div>
<div id="main"></div>
<div id="stacktrace" class="x-hide-visibility">
<pre>java.lang.ClassNotFoundException: org.seasar.framework.container.external.GenericS2ContainerInitializer
	at java.net.URLClassLoader$1.run(URLClassLoader.java:200)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.net.URLClassLoader.findClass(URLClassLoader.java:188)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:306)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:251)
	at org.seasar.dolteng.eclipse.util.S2ContainerUtil.createS2Container(S2ContainerUtil.java:174)
	at org.seasar.dolteng.eclipse.util.S2ContainerUtil.loadFromClassLoader(S2ContainerUtil.java:151)
	at org.seasar.dolteng.eclipse.util.S2ContainerUtil.loadNamingConvensions(S2ContainerUtil.java:59)
	at org.seasar.dolteng.eclipse.preferences.impl.DoltengPreferencesImpl.setUpValues(DoltengPreferencesImpl.java:99)
	at org.seasar.dolteng.eclipse.preferences.impl.DoltengPreferencesImpl.<init>(DoltengPreferencesImpl.java:87)
	at org.seasar.dolteng.eclipse.nature.DoltengNature.init(DoltengNature.java:92)
	at org.seasar.dolteng.eclipse.nature.DoltengNature.getProjectPreferences(DoltengNature.java:85)
	at org.seasar.dolteng.eclipse.DoltengCore.getPreferences(DoltengCore.java:101)
	at org.seasar.dolteng.eclipse.util.ElementMarkingWalker$1.visit(ElementMarkingWalker.java:53)
	at org.eclipse.core.internal.events.ResourceDelta.accept(ResourceDelta.java:67)
	at org.eclipse.core.internal.events.ResourceDelta.accept(ResourceDelta.java:76)
	at org.eclipse.core.internal.events.ResourceDelta.accept(ResourceDelta.java:76)
	at org.eclipse.core.internal.events.ResourceDelta.accept(ResourceDelta.java:76)
	at org.eclipse.core.internal.events.ResourceDelta.accept(ResourceDelta.java:48)
	at org.seasar.dolteng.eclipse.util.ElementMarkingWalker.walk(ElementMarkingWalker.java:48)
	at org.seasar.dolteng.eclipse.marker.DIMapper.elementChanged(DIMapper.java:60)
	at org.eclipse.jdt.internal.core.DeltaProcessor$3.run(DeltaProcessor.java:1458)
	at org.eclipse.core.runtime.SafeRunner.run(SafeRunner.java:37)
	at org.eclipse.jdt.internal.core.DeltaProcessor.notifyListeners(DeltaProcessor.java:1448)
	at org.eclipse.jdt.internal.core.DeltaProcessor.firePostChangeDelta(DeltaProcessor.java:1296)
	at org.eclipse.jdt.internal.core.DeltaProcessor.fire(DeltaProcessor.java:1275)
	at org.eclipse.jdt.internal.core.DeltaProcessor.resourceChanged(DeltaProcessor.java:1833)
	at org.eclipse.jdt.internal.core.DeltaProcessingState.resourceChanged(DeltaProcessingState.java:444)
	at org.eclipse.core.internal.events.NotificationManager$2.run(NotificationManager.java:280)
	at org.eclipse.core.runtime.SafeRunner.run(SafeRunner.java:37)
	at org.eclipse.core.internal.events.NotificationManager.notify(NotificationManager.java:274)
	at org.eclipse.core.internal.events.NotificationManager.broadcastChanges(NotificationManager.java:148)
	at org.eclipse.core.internal.resources.Workspace.broadcastPostChange(Workspace.java:256)
	at org.eclipse.core.internal.resources.Workspace.endOperation(Workspace.java:958)
	at org.eclipse.core.internal.resources.Workspace.run(Workspace.java:1746)
	at org.eclipse.ui.actions.DeleteResourceAction$3.run(DeleteResourceAction.java:596)
	at org.eclipse.core.internal.jobs.Worker.run(Worker.java:58)
</pre>
</div>
</body>
</html>
