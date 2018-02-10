import React from 'react';

class Card extends React.Component {
	constructor(props) {
		super(props);
		this.contents = props.contents;
		this.showDetailModal = () => {
			this.contents.showDetailModal(this.props);
		}
		this.colorClass = 'thumbnail alert-success';
		if (this.props.type === 'R') {
			this.colorClass = 'thumbnail alert-warning';
		}
		this.onMouseOver = () => {
			this.thumbnameDiv.className = 'thumbnail';
			this.thumbnameDiv.style.cursor = 'pointer';
		}
		this.onMouseOut = () => {
			this.thumbnameDiv.className = this.colorClass;
			this.thumbnameDiv.style.cursor = 'arrow';
		}
	}

	render() {
		var className = 'thumbnail ' + this.colorClass;
		return (
			<div ref={ref => this.thumbnameDiv = ref} className={className} onClick={this.showDetailModal} onMouseOver={this.onMouseOver} onMouseOut={this.onMouseOut}>
				<div className='caption'>
					<h3 style={{paddingRight:'20px'}}>
						<span name='title'>{this.props.title}</span>
						<img name='image' className='navbar-right' src={this.props.image} width='50px' style={{float:'right'}}/>
					</h3>
					<h5 name='author'>{this.props.author}</h5>
					<p name='text'>{this.props.text}</p>
					<p><span name='nickname'>{this.props.nickname}</span> <span name='updateDate'>{this.props.updateDate}</span></p>
					<input type='hidden' name='type' value={this.props.type}/>
				</div>
			</div>
		);
	}
}

export default Card;