import React from 'react';
import Card from './Card'

class Body extends React.Component {
	constructor(props) {
		super(props);
		this.state = {list : []};
		this.contents = props.contents;
		this.maxColumnCount = 4;
		this.lastColIndex = 0;

		this.range = props.range; // all / my
		this.contentsDivId = this.range + 'contents';
		this.pageIndex = 0;
		this.query = ''; 
		this.isbn = ''; // 쿼리로 검색 후 검색에 사용된 isbn을 저장해 뒀다가 스크롤이동 시 재사용 한다.

		this.isLoading = false;
		this.isRefresh = false;
		this.isLastPage = false;
		this.pageSize = 30;

		setInterval(() => {
			if ((window.innerHeight + window.scrollY) >= (document.body.offsetHeight - 500)) {
				this.getListByScroll();
			}
		}, 500);
	}

	getListByScroll() {
		if (!this.isLoading) {
			this.isRefresh = false;
			this.getList({isbn: this.isbn, range: this.range, pageIndex: this.pageIndex, pageSize: this.pageSize});
		}
	}

	refresh() {
		this.isLastPage = false;
		this.isRefresh = true;
		this.getList({isbn: this.isbn, range: this.range, pageIndex: 0, pageSize: this.pageSize});
	}

	getListBySearchInput(query) {
		this.isLastPage = false;
		this.isRefresh = true;
		this.getList({query: query, range: this.range, pageIndex: 0, pageSize: this.pageSize});
	}

	getList(data) {
		if ($('#mainView').is(":visible") && this.range === 'my') {
			return;
		} else if (!$('#mainView').is(":visible") && this.range === 'all') {
			return;
		}

		if (this.isLastPage) {
			return;
		}

		this.isLoading = true;
		var self = this;
		$.ajax({
				type: 'GET',
				url: '/api/search/contents-list',
				dataType: 'json',
				data: data,
			}).done(function(res) {
				self.isbn = res.isbn;
				
				var newList = [];
				for (var i in res.list) {
					var d = res.list[i];
					
					newList.push({
						reviewId: d.ID,
						nickname: d.NICKNAME,
						title: d.TITLE,
						author: d.AUTHOR,
						updateDate: d.UPDATE_DATE,
						image: d.IMAGE,
						text: d.TEXT,
						type: d.TYPE // C : Coment, R : review
					});
				}
				
				self.setState({list : newList});
				self.isLoading = false;
				++self.pageIndex;
				self.isLastPage = res.list.length < self.pageSize;
			}).fail(function() {
				self.isLoading = false;
				alert('Server error.');
			});
	}

	makeContentsRow(col, data, contentsCount) {
		data.key = 'contentsCard-' + contentsCount;
		this.lastColIndex = contentsCount % this.maxColumnCount;
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
				<Card key={data.key}
					reviewId={data.reviewId}
					nickname={data.nickname}
					title={data.title}
					author={data.author}
					updateDate={data.updateDate}
					image={data.image}
					text={data.text}
					type={data.type}
					contents={this.contents}/>
			</div>
		);
	}

	render() {
		var currentRows = $('#' + this.contentsDivId).children();
		var contentsCount = 0;
		var colInfoArray = [];
		var self = this;

		if (this.isRefresh) {
			return (
				<div id={self.contentsDivId} style={{paddingTop:'20px', paddingLeft:'15px', paddingRight:'15px'}}>
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
		} else {
			return (
				<div id={self.contentsDivId} style={{paddingTop:'20px', paddingLeft:'15px', paddingRight:'15px'}}>
					{
						currentRows.map(function(index) {
							var resultRow = null;
							var column = currentRows[index].firstChild;
							while (column) {
								var contentsParent = column.firstChild.firstChild;
								var data = {};
								data.title = $(contentsParent).find('span[name="title"]').text();
								data.author = $(contentsParent).children('h5[name="author"]').text();
								data.nickname = $(contentsParent).find('span[name="nickname"]').text();
								data.updateDate = $(contentsParent).find('span[name="updateDate"]').text();
								data.image = $(contentsParent).find('img[name="image"]').attr('src');
								data.text = $(contentsParent).children('p[name="text"]').text();
								data.type = $(contentsParent).children('input[name="type"]').val();
								
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
}

export default Body;