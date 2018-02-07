import React from 'react';

class ContentsCard extends React.Component {
	constructor(props) {
		super(props);
	}

	componentDidMount() {
		$('.thumbnail').mouseover(function() {
			$(this).addClass('alert-info');
			$(this).css('cursor', 'pointer');

		});
		$('.thumbnail').mouseout(function() {
			$(this).removeClass('alert-info');
			$(this).css('cursor', 'arrow');
		});
	}

	render() {
		return (
			<div className='thumbnail'>
				<div className='caption'>
					<h3 style={{paddingRight:'20px'}}>
						<span name='title'>{this.props.title}</span>
						<img name='image' className='navbar-right' src={this.props.image} width='50px' style={{float:'right'}}/>
					</h3>
					<h5 name='author'>{this.props.author}</h5>
					<p name='impression'>{this.props.impression}</p>
					<p><span name='nickname'>{this.props.nickname}</span> <span name='updateDate'>{this.props.updateDate}</span></p>
					<input type='hidden' name='title' value={this.props.title}/>
				</div>
			</div>
		);
	}
}

export default ContentsCard;