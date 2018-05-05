import React from 'react';
import Card from './Card'

class Body extends React.Component {
	constructor(props) {
		super(props);
		this.state = {list : []};
		this.parent = props.parent;
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

		$(window).scroll(() => {
			var maxHeight = $(document).height();
    		var currentScroll = $(window).scrollTop() + $(window).height();

		    if (maxHeight <= currentScroll + 200) {
		    	this.getListByScroll();
		    } 
		});
	}

	getListByScroll() {
		if (!this.isLoading) {
			this.isRefresh = false;
			this.getList({isbn: this.isbn, range: this.range, pageIndex: this.pageIndex * this.pageSize, pageSize: this.pageSize});
		}
	}

	refresh() {
		this.setState({list : []});
		this.isLastPage = false;
		this.isRefresh = true;
		this.getList({isbn: this.isbn, range: this.range, pageIndex: 0, pageSize: this.pageSize});
	}

	getListBySearchInput(query) {
		this.setState({list : []});
		this.isLastPage = false;
		this.isRefresh = true;
		this.getList({query: query, range: this.range, pageIndex: 0, pageSize: this.pageSize});
	}

	getList(data) {
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
				// console.log(d);
				
				var profileImage = '/css/default_profile.png';
				if (d.PROFILE_IMAGE && d.PROFILE_IMAGE.length > 0) {
					profileImage = d.PROFILE_IMAGE;
				}

				newList.push({
					contentsId: d.ID,
					bookId: d.BOOK_ID,
					nickname: d.NICKNAME,
					profileImage: profileImage,
					title: d.TITLE,
					author: d.AUTHOR,
					publisher: d.PUB,
					image: d.IMAGE,
					text: d.TEXT,
					type: d.TYPE, // C : Coment, R : review
					my: d.USER_ID === res.id ? 'y' : 'n'
				});
			}

			self.isLoading = false;
			++self.pageIndex;
			self.isLastPage = res.list.length < self.pageSize;
			self.setState({list : newList});
		}).fail(function(data) {
			self.isLoading = false;
			if (data.status == 401) {
				document.location = "/signin.html";
			} else {
				location.reload();
			}
		});
	}

	makeContentsRow(col, data, contentsCount) {
		data.key = 'contentsCard-' + contentsCount;
		this.lastColIndex = contentsCount % this.maxColumnCount;
		col[this.lastColIndex] = data;

		var newRow = null;
		if (contentsCount > 0 && contentsCount % this.maxColumnCount == this.maxColumnCount - 1) {
			this.lastColIndex = -1;
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
				<Card key={data.key} data={data} parent={this.parent}/>
			</div>
		);
	}

	render() {
		var contentsCount = 0;
		var colInfoArray = [];
		var self = this;

		if (this.isRefresh) {
			return (
				<div id={self.contentsDivId} style={{paddingTop:'20px', maxWidth:'1200px'}}>
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
			var currentRows = $('#' + this.contentsDivId).children();
			return (
				<div id={self.contentsDivId} style={{paddingTop:'20px', maxWidth:'1200px'}}>
					{
						currentRows.map(function(index) {
							var resultRow = null;
							var column = currentRows[index].firstChild;
							while (column) {
								var data = {};
								data.title = $(column).find('h5[name="title"]').text();
								data.author = $(column).find('span[name="author"]').text();
								data.publisher = $(column).find('span[name="publisher"]').text();
								data.nickname = $(column).find('span[name="nickname"]').text();
								data.profileImage = $(column).find('img[name="profileImage"]').attr('src');
								data.image = $(column).find('img[name="image"]').attr('src');
								data.text = $(column).find('span[name="text"]').text();
								data.contentsId = $(column).find('input[name="contentsId"]').val();
								data.bookId = $(column).find('input[name="bookId"]').val();
								data.type = $(column).find('input[name="type"]').val();
								data.my = $(column).find('input[name="my"]').val();
								
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