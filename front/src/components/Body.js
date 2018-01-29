import React from 'react';
import ContentsCard from './ContentsCard'

class Body extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			list : []
		}
		this.detectScrollPosition = () => {
			if ((window.innerHeight + window.scrollY) >= (document.body.offsetHeight - 500)) {
				this.getList();
			}
		}
		this.maxColumnCount = 4;
		this.lastColIndex = 0;
	}

	componentDidMount() {
		window.addEventListener('scroll', this.detectScrollPosition, false);
		this.getList();
	}

	componentWillUnmount() {
		window.removeEventListener('scroll', this.detectScrollPosition, false);
	}

	getList() {
		var newList = [];
		for (var i = 0; i < 10; ++i) {
			newList.push({
				'nickname': 'nickname' + i,
				'title': 'title' + i,
				'author': 'author' + i,
				'updateDate': 'updateDate' + i
			});
		}
		
		this.setState({list : newList});
	}

	makeContentsRow(col, data, contentsCount) {
		this.lastColIndex = contentsCount % this.maxColumnCount;
		data.key = 'contentsCard-' + contentsCount;
		col[this.lastColIndex] = data;

		var newRow = null;
		if (contentsCount > 0 && contentsCount % this.maxColumnCount == this.maxColumnCount - 1) {
			newRow = (
				<div className='row' key={'row-' + contentsCount}>
					{this.makeContentsCard(col[0])}
					{this.makeContentsCard(col[1])}
					{this.makeContentsCard(col[2])}
					{this.makeContentsCard(col[3])}
				</div>
			);
		}

		return newRow;
	}

	makeContentsCard(data) {
		return (
			<div className='col-sm-6 col-md-3' key={'col-' + data.key}>
				<ContentsCard key={data.key}
					nickname={data.nickname}
					title={data.title}
					author={data.author}
					updateDate={data.updateDate}/>
			</div>
		);
	}

	render() {
		var currentRows = $('#contents').children();
		var contentsCount = 0;
		var colInfoArray = [];
		var self = this;

		return (
			<div id='contents' style={{paddingTop:'10px', paddingLeft:'15px', paddingRight:'15px'}}>
				{
					currentRows.map(function(index) {
						var resultRow = null;
						var column = currentRows[index].firstChild;
						while (column) {
							var contentsParent = column.firstChild.firstChild;
							var data = {};
							data.title = $(contentsParent).children('input[name="title"]').val();
							data.author = $(contentsParent).children('h5[name="author"]').text();
							data.nickname = $(contentsParent).children('input[name="nickname"]').val();
							data.updateDate = $(contentsParent).children('input[name="updateDate"]').val();
							
							var newRow = self.makeContentsRow(colInfoArray, data, contentsCount++);
							if (newRow != null) {
								resultRow = newRow;
							}
							column = column.nextSibling;
						}
						return resultRow;
					})
				}
				{
					this.state.list.map(function(data) {
						return self.makeContentsRow(colInfoArray, data, contentsCount++);
					})
				}
				<div className='row' key={'row-' + contentsCount}>
					{
						colInfoArray.slice(0, this.lastColIndex + 1).map(function(data) {
							return self.makeContentsCard(data);
						})
					}
				</div>
			</div>
		);
	}
}

export default Body;