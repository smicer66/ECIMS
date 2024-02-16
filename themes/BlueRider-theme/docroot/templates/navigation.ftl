<nav class="navbar navbar-inverse" role="navigation">
  <div class="container-fluid">
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
		<ul>
			#foreach ($nav_item in $nav_items)
				#if ($nav_item.isSelected())
					<li class="active">
				#else
					<li>
				#end
					<a href="$nav_item.getURL()" $nav_item.getTarget()><span>$nav_item.icon() $nav_item.getName()</span></a>
	
					#if ($nav_item.hasChildren())
						<ul class="child-menu">
							#foreach ($nav_child in $nav_item.getChildren())
								#if ($nav_child.isSelected())
									<li class="active">
								#else
									<li>
								#end
									<a href="$nav_child.getURL()" $nav_child.getTarget()>$nav_child.getName()</a>
								</li>
							#end
						</ul>
					#end
				</li>
			#end		
		</ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>