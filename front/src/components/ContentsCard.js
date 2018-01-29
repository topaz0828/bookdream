import React from 'react';

class ContentsCard extends React.Component {
	render() {
		return (
			<a href='#' className='thumbnail'>
				<div className='caption'>
					<h3 name='title' style={{paddingRight:'20px'}}>{this.props.title} <img className='navbar-right' src="https://scontent.xx.fbcdn.net/v/t1.0-1/p100x100/15094935_1225609307512845_7310823645782503183_n.jpg?oh=697e14377cecfe09c81a08c85cd7576e&oe=5AD98CB3" width='50px'/></h3>
					<h5 name='author'>{this.props.author}</h5>
					<p name='contents'>Cras justo odio, dapibus ac facilisis in, egestas eget quam. Donec id elit non mi porta gravida at eget metus. Nullam id dolor id nibh ultricies vehicula ut id elit.</p>
					<p>{this.props.nickname} {this.props.updateDate}</p>
					<input type='hidden' name='title' value={this.props.title}/>
					<input type='hidden' name='nickname' value={this.props.nickname}/>
					<input type='hidden' name='updateDate' value={this.props.updateDate}/>
				</div>
			</a>
		);
	}
}

export default ContentsCard;