import React from 'react';
import ContentsCard from './ContentsCard'

class Body extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			list : []
		}
	}

	componentDidMount() {
		window.addEventListener('scroll', () => {
			if ((window.innerHeight + window.scrollY) >= (document.body.offsetHeight - 500)) {
				this.getList();
			}
		}, false);
		this.getList();
	}

	componentWillUnmount() {
		window.removeEventListener('scroll', () => {
			if ((window.innerHeight + window.scrollY) >= (document.body.offsetHeight - 500)) {
				this.getList();
			}
		}, false);
	}

	getList() {
		var newList = [];
		for (var i = 0; i < 50; ++i) {
			newList.push(i);
		}
		
		this.setState({
			list : newList
		});
	}

	makeContentsCard(col, data) {
		var key = "row-" + data;
		var newRow = null;
		if (data > 0 && data % 4 == 0) {
			newRow = (
				<div className="row" key={key}>
					{col[0]}
					{col[1]}
					{col[2]}
					{col[3]}
				</div>
			);
		}

		col[data % 4] = (
			<div className="col col-3 contents">
				<ContentsCard count={data}/>
			</div>
		);

		return newRow;
	}

	render() {
		var currentRows = $("#contents").children();
		var childCount = 0;
		var colInfoArray = [];
		var self = this;

		return (
			<div id="contents" style={{paddingTop:"10px"}}>
				{
					currentRows.map(function(index) {
						var resultRow = null;
						var column = currentRows[index].firstChild;
						while (column) {
							var newRow = self.makeContentsCard(colInfoArray, childCount++);
							if (newRow != null) {
								resultRow = newRow;
							}
							column = column.nextSibling;
						}
						return resultRow;
					})
				}
				{
					this.state.list.map(function(index) {
						return self.makeContentsCard(colInfoArray, childCount + index);
					})
				}
			</div>
		);
	}
}

export default Body;