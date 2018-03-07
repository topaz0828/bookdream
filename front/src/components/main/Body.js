import React from 'react';
import Card from './Card'

class Body extends React.Component {
	constructor(props) {
		super(props);
		this.state = {list : []};
		this.detectScrollPosition = () => {
			if ((window.innerHeight + window.scrollY) >= (document.body.offsetHeight - 500)) {
				this.getListByScroll();
			}
		}
		this.contents = props.contents;
		this.maxColumnCount = 4;
		this.lastColIndex = 0;
		this.contentsDivId = this.range + 'contents';

		this.isRefresh = false;
		this.range = props.range; // all / my
		this.pageIndex = 0;
		this.query = ''; 
		this.isbn = []; // 쿼리로 검색 후 검색에 사용된 isbn을 저장해 뒀다가 스크롤이동 시 재사용 한다.
	}

	componentDidMount() {
		window.addEventListener('scroll', this.detectScrollPosition, false);
	}

	componentWillUnmount() {
		window.removeEventListener('scroll', this.detectScrollPosition, false);
	}

	getListByScroll() {
		this.isRefresh = false;
		this.getList({isbn: [], range: this.range, pageIndex: this.pageIndex});
	}

	getListBySearchInput(query) {
		this.isRefresh = true;
		this.getList({query: query, range: this.range, pageIndex: this.pageIndex});
	}

	getList(data) {
		var self = this;
		$.ajax({
				type: 'GET',
				url: '/api/search/contents-list',
				dataType: 'json',
				data: data,
			}).done(function(res) {
				var newList = [];
				for (var i = 0; i < 5; ++i) {
					newList.push({
						reviewId: 'reviewId' + i,
						nickname: 'nickname' + i,
						title: 'title' + i,
						author: 'author' + i,
						updateDate: 'updateDate' + i,
						image: 'https://scontent.xx.fbcdn.net/v/t1.0-1/p100x100/15094935_1225609307512845_7310823645782503183_n.jpg?oh=697e14377cecfe09c81a08c85cd7576e&oe=5AD98CB3',
						text: '핵 감명깊은 문구다.',
						type: 'I' // I : impression, R : review
					});
				}
				
				self.setState({list : newList});
			}).fail(function() {
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